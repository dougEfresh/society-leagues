package com.society.leagues.service;

import com.society.leagues.client.api.domain.*;
import com.society.leagues.listener.DaoListener;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
public class StatService {
    final static Logger logger = Logger.getLogger(StatService.class);
    final AtomicReference<List<Stat>> lifetimeStats = new AtomicReference<>(new CopyOnWriteArrayList<Stat>());
    final AtomicReference<Map<Season,List<Stat>>> userSeasonStat = new AtomicReference<>(new HashMap<>());
    final AtomicReference<List<Stat>> handicapStats = new AtomicReference<>(new CopyOnWriteArrayList<Stat>());
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
                if (object instanceof TeamMatch)
                    refreshTeamMatchStats((TeamMatch) object);
            }

            @Override
            public void onChange(LeagueObject object) {
                if (object instanceof TeamMatch)
                    refreshTeamMatchStats((TeamMatch) object);

                if (object instanceof PlayerResult)
                    refresh();
            }

            @Override
            public void onDelete(LeagueObject object) {
                if (object instanceof TeamMatch)
                    refreshTeamMatchStats((TeamMatch) object);

                if (object instanceof PlayerResult)
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
        List<Stat> teamStats = new ArrayList<>();
        leagueService.findCurrent(Team.class).parallelStream().forEach(s -> teamStats.add(s.getStats()));
        return teamStats;
    }

    @Scheduled(fixedRate = 1000*60*60, initialDelay = 1000*60*11)
    public void refresh() {
        if (!enableRefresh)
            return;
        if (threadPoolTaskExecutor.getActiveCount() > 0) {
            logger.info("Skipping refresh");
            return;
        }
        logger.info("Submitting to task for refresh");
        threadPoolTaskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                logger.info("Refreshing stats");
                long start = System.currentTimeMillis();
                final Set<Team> teams = leagueService.findCurrent(Team.class);
                for (Team team : teams) {
                    refreshTeamStats(team);
                }
                refreshTeamRank();
                refreshUserSeasonStats();
                refreshUserLifetimeStats();
                rereshUserHandicapStats();
                resultService.refresh();
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
                s.setSeason(season);
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
                stats.add(Stat.buildHandicapStats(w, StatType.HANDICAP_WINS, user, handicap));
            }
        }
        handicapStats.lazySet(stats);
    }

    public void refreshTeamMatchStats(final TeamMatch tm) {
        if (threadPoolTaskExecutor.getActiveCount() > 1) {
            logger.info("Skipping team refresh");
            return;
        }
        logger.info("Submitting to task for team refresh");
        threadPoolTaskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                refreshTeamStats(tm.getHome());
                refreshTeamStats(tm.getAway());
                refreshTeamRank();
            }
        });
    }

    public void refreshTeamRank() {
        Map<Season, List<Team>> active =  leagueService.findCurrent(Team.class).stream()
                .collect(Collectors.groupingBy(s -> s.getSeason(), Collectors.toList()));

        for (Season season : active.keySet()) {
            List<Team> ranks = active.get(season);
            List<Team> rankings = ranks.stream().sorted(new Comparator<Team>() {
                @Override
                public int compare(Team t, Team t1) {
                    if (!Objects.equals(t1.getStats().getWins(), t.getStats().getWins())) {
                        return t1.getStats().getWins().compareTo(t.getStats().getWins());
                    }
                    if (!Objects.equals(t1.getStats().getLoses(), t.getStats().getLoses())) {
                        return t1.getStats().getLoses().compareTo(t1.getStats().getLoses());
                    }
                    if (!Objects.equals(t1.getStats().getRacksWon(), t.getStats().getRacksWon())) {
                        return t1.getStats().getRacksWon().compareTo(t.getStats().getRacksWon());
                    }
                    return t.getStats().getRacksLost().compareTo(t1.getStats().getRacksLost());
                }
            }).collect(Collectors.toList());
            int rank = 0;
            for (Team t : rankings) {
                Integer old = t.getRank();
                t.setRank(++rank);
                t.getStats().setRank(t.getRank());
                if (!old.equals(t.getRank())) {
                    leagueService.save(t);
                }
            }
        }
    }

    public void refreshPlayerResult(PlayerResult pr) {

    }

    public void refreshTeamStats(Team team) {
        final Set<TeamMatch>  teamMatches = leagueService.findCurrent(TeamMatch.class);
        Stat stat =  Stat.buildTeamStats(team,
                teamMatches.parallelStream()
                        .filter(tm -> tm.hasTeam(team))
                        .filter(TeamMatch::isHasResults)
                        .collect(Collectors.toList())
        );

        if (Stat.isDifferent(stat,team.getStats())) {
            team.setStats(stat);
            leagueService.save(team);
        }
    }

    public void setEnableRefresh(boolean enableRefresh) {
        this.enableRefresh = enableRefresh;
    }
}
