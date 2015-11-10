package com.society.leagues.service;

import com.society.leagues.client.api.domain.*;
import com.society.leagues.listener.DaoListener;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
public class StatService {
    final static Logger logger = Logger.getLogger(StatService.class);
    final AtomicReference<List<Stat>> teamStats = new AtomicReference<>(new ArrayList<>(100));
    final AtomicReference<List<Stat>> lifetimeStats = new AtomicReference<>(new ArrayList<>(1000));
    final AtomicReference<Map<Season,List<Stat>>> userSeasonStat = new AtomicReference<>(new HashMap<>());
    final AtomicReference<List<Stat>> handicapStats = new AtomicReference<>(new ArrayList<>(2000));
    @Autowired ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired LeagueService leagueService;
    boolean enableRefresh = true;
    @Autowired ResultService resultService;

    @PostConstruct
    public void init() {
        threadPoolTaskExecutor.setCorePoolSize(1);
        threadPoolTaskExecutor.setMaxPoolSize(2);
        threadPoolTaskExecutor.setQueueCapacity(2);
        leagueService.addListener(new DaoListener() {
            @Override
            public void onAdd(LeagueObject object) {
                if (object instanceof TeamMatch || object instanceof PlayerResult)
                    refresh();
            }

            @Override
            public void onChange(LeagueObject object) {
                if (object instanceof TeamMatch || object instanceof PlayerResult)
                    refresh();
            }

            @Override
            public void onDelete(LeagueObject object) {
                if (object instanceof TeamMatch || object instanceof PlayerResult)
                    refresh();
            }
        });
        refresh();
    }

    public List<Stat> getLifetimeStats() {
        return lifetimeStats.get();
    }

    public Map<Season,List<Stat>>  getUserSeasonStats() {
        return this.userSeasonStat.get();
    }

    public List<Stat> getTeamStats() {
        return teamStats.get();
    }

    @Scheduled(fixedRate = 1000*60*60, initialDelay = 1000*60*11)
    public void refresh() {
        if (!enableRefresh)
            return;
        if (threadPoolTaskExecutor.getActiveCount() >0) {
            return;
        }
        threadPoolTaskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    return;
                }
                logger.info("Refreshing stats");
                long start = System.currentTimeMillis();
                final List<Team> teams = leagueService.findAll(Team.class);
                final List<TeamMatch>  teamMatches = leagueService.findAll(TeamMatch.class);
                final List<Stat> ts = new ArrayList<>();
                for (Team team : teams) {
                    ts.add(Stat.buildTeamStats(team,
                            teamMatches.parallelStream()
                                    .filter(tm -> tm.hasTeam(team))
                                    .filter(TeamMatch::isHasResults)
                                    .collect(Collectors.toList())
                    ));
                }
                Map<Season, List<Stat>> active = ts.parallelStream().filter(s -> s.getSeason().isActive()).collect(Collectors.groupingBy(s -> s.getSeason(), Collectors.toList()));
                for (Season season : active.keySet()) {
                    List<Stat> ranks = active.get(season);
                    List<Stat> rankings = ranks.stream().sorted(new Comparator<Stat>() {
                        @Override
                        public int compare(Stat stat, Stat t1) {
                            if (!Objects.equals(t1.getWins(), stat.getWins())) {
                                return t1.getWins().compareTo(stat.getWins());
                            }
                            if (!Objects.equals(t1.getLoses(), stat.getLoses())) {
                                return stat.getLoses().compareTo(t1.getLoses());
                            }
                            if (stat.getSeason().isNine()) {

                            }
                            if (!Objects.equals(t1.getRacksWon(), stat.getRacksWon())) {
                                return t1.getRacksWon().compareTo(stat.getRacksWon());
                            }
                            return stat.getRacksLost().compareTo(t1.getRacksLost());
                        }
                    }).collect(Collectors.toList());
                    int rank = 0;
                    for (Stat t : rankings) {
                        Integer old = t.getTeam().getRank();
                        t.getTeam().setRank(++rank);
                        if (!old.equals(t.getTeam().getRank()))
                            leagueService.save(t.getTeam());
                    }
                }
                teamStats.lazySet(ts);
                refreshUserSeasonStats();
                refreshUserLifetimeStats();
                rereshUserHandicapStats();
                logger.info("Done Refreshing stats  (" + (System.currentTimeMillis() - start) + "ms)");
            }
        });
    }

    private void refreshUserSeasonStats() {
        List<Season> seasons = leagueService.findAll(Season.class);
        Map<Season,List<Stat>> userSeasonStats = new HashMap<>(1000);
        for (Season season : seasons) {
            List<PlayerResult> results = leagueService.findAll(PlayerResult.class).stream().parallel().
                    filter(pr -> pr.getSeason().equals(season)).
                    collect(Collectors.toList());
            Map<User, List<PlayerResult>> losers = results.stream().filter(r->r.getLoser() != null).collect(Collectors.groupingBy(r -> r.getLoser(), Collectors.toList()));
            Map<User, List<PlayerResult>> winners = results.stream().filter(r->r.getWinner() != null).collect(Collectors.groupingBy(r -> r.getWinner(), Collectors.toList()));
            Map<User, List<PlayerResult>> all = new HashMap<>();
            for (User user : winners.keySet()) {
                all.put(user, winners.get(user));
            }
            for (User user : losers.keySet()) {
                if (all.containsKey(user)) {
                    all.get(user).addAll(losers.get(user));
                } else {
                    all.put(user, losers.get(user));
                }
            }
            List<Stat> stats = new ArrayList<>(100);
            List<Team> teams = leagueService.findAll(Team.class).stream().filter(t->t.getSeason().equals(season)).collect(Collectors.toList());
            for (User user : all.keySet()) {
                Stat s = Stat.buildPlayerSeasonStats(user,
                        season,
                        all.get(user)
                );
                s.setTeam(teams.stream().filter(t->t.hasUser(user)).findFirst().orElse(null));
                stats.add(s);
            }
            userSeasonStats.put(season,stats);
        }
        for (Season season : userSeasonStats.keySet()) {
            List<Stat> stats = userSeasonStats.get(season);
             userSeasonStats.put(season, stats.stream().sorted(new Comparator<Stat>() {
                 @Override
                 public int compare(Stat stat, Stat t1) {
                     if (!t1.getWins().equals(stat.getWins())) {
                         return t1.getWins().compareTo(stat.getWins());
                     }
                     if (!t1.getLoses().equals(stat.getLoses())) {
                         return stat.getLoses().compareTo(t1.getLoses());
                     }

                     if (!t1.getRacksLost().equals(stat.getRacksLost())) {
                         return stat.getRacksLost().compareTo(t1.getRacksLost());
                     }
                     return t1.getWinPct().compareTo(stat.getWinPct());
                 }
             }).collect(Collectors.toList()));
            int rank = 0;
            for (Stat stat : userSeasonStats.get(season)) {
                stat.setRank(++rank);
            }
        }

        this.userSeasonStat.lazySet(userSeasonStats);

        resultService.refresh();
    }

    private void refreshUserLifetimeStats() {
        logger.info("Refreshing User stats");
        List<User> users = leagueService.findAll(User.class);
        List<Stat> lifeStats = new ArrayList<>(1000);
        for (User user : users) {
            for (List<Stat> stats : this.userSeasonStat.get().values()) {
                lifeStats.add(Stat.buildLifeTimeStats(user, stats.stream().filter(s -> s.getUser().equals(user)).collect(Collectors.toList())));
            }
        }
        lifetimeStats.lazySet(lifeStats);
        logger.info("Created " + lifetimeStats.get().size() + " stats for " + users.size() + " users with a total of " + lifeStats.size());
    }

    private void rereshUserHandicapStats() {
        Map<User,List<PlayerResult>> winners = leagueService.findCurrent(PlayerResult.class).stream().
                collect(Collectors.groupingBy(PlayerResult::getWinner));
        Map<User,List<PlayerResult>> loser = leagueService.findCurrent(PlayerResult.class).stream().
                collect(Collectors.groupingBy(PlayerResult::getLoser));

        List<Stat> stats = new ArrayList<>(1000);
        for (User user : winners.keySet()) {
            for (Handicap handicap : Handicap.values()) {
                List<PlayerResult> w = winners.get(user).stream().filter(p->p.getLoserHandicap() == handicap).collect(Collectors.toList());
                if (w.isEmpty()) {
                    continue;
                }
                stats.add(Stat.buildHandicapStats(w,StatType.HANDICAP_WINS,user,handicap));
            }
        }
        for (User user : loser.keySet()) {
            for (Handicap handicap : Handicap.values()) {
                List<PlayerResult> w = loser.get(user).stream()
                        .filter(p -> p.getWinnerHandicap() == handicap)
                        .collect(Collectors.toList());
                if (w.isEmpty()) {
                    continue;
                }
                stats.add(Stat.buildHandicapStats(w,StatType.HANDICAP_WINS,user,handicap));
            }
        }
        handicapStats.lazySet(stats);
    }

    public void setEnableRefresh(boolean enableRefresh) {
        this.enableRefresh = enableRefresh;
    }
}
