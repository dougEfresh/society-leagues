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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
public class StatService {
    final static Logger logger = Logger.getLogger(StatService.class);
    final AtomicReference<List<Stat>> lifetimeStats = new AtomicReference<>(new CopyOnWriteArrayList<Stat>());
    final AtomicReference<List<Stat>> lifetimeDivisionStats = new AtomicReference<>(new CopyOnWriteArrayList<Stat>());
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
                if (object instanceof TeamMatch) {
                    refreshTeamMatchStats((TeamMatch) object);
                }
                if (object instanceof PlayerResult) {
                    refresh();
                }
            }

            @Override
            public void onChange(LeagueObject object) {
                if (object instanceof TeamMatch)
                    refreshTeamMatchStats((TeamMatch) object);

                if (object instanceof PlayerResult) {
                    refreshTeamMatch(((PlayerResult) object).getTeamMatch());
                    refresh();
                }
            }

            @Override
            public void onDelete(LeagueObject object) {
                if (object instanceof TeamMatch)
                    refreshTeamMatchStats((TeamMatch) object);

                if (object instanceof PlayerResult) {
                    refresh();
                }
            }
        });
        refresh();
    }


    public List<Stat> getLifetimeDivisionStats() {
        return lifetimeDivisionStats.get();
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

    @Scheduled(fixedRate = 1000*60*5, initialDelay = 1000*60*11)
    public void refresh() {
        if (!enableRefresh)
            return;
        logger.info("Refreshing stats");
        long start = System.currentTimeMillis();
        final Set<Team> teams = leagueService.findCurrent(Team.class);
        long startTime = System.currentTimeMillis();

        startTime = System.currentTimeMillis();
        logger.info("RefreshTeamMatch ");
        leagueService.findCurrent(TeamMatch.class).parallelStream().forEach(StatService.this::refreshTeamMatch);
        logger.info("RefreshTeamMatch " + (System.currentTimeMillis() - startTime));

        startTime = System.currentTimeMillis();
        logger.info("RefreshTeam TeamRank ");
        //refreshTeamRank();
        logger.info("RefreshTeam TeamRank " + (System.currentTimeMillis() - startTime));
        startTime = System.currentTimeMillis();
        logger.info("UserSesonStats  ");
        refreshUserSeasonStats(true);
        logger.info("UserSesonStats  " + (System.currentTimeMillis() - startTime));
        //refreshUserLifetimeStats();
        rereshUserHandicapStats();
        logger.info("RefreshTeam Stats ");
        for (Team team : teams) {
            refreshTeamStats(team);
        }
        logger.info("RefreshTeam Stats " + (System.currentTimeMillis() - startTime));
        resultService.refresh();
        logger.info("Done Refreshing stats  (" + (System.currentTimeMillis() - start) + "ms)");

        List<User> users = leagueService.findAll(User.class);
        logger.info("Processing lifetime stats");
        List<Stat> lifeDivisionStats = new ArrayList<>(500);
        List<PlayerResult> results = leagueService.findAll(PlayerResult.class);
        for (User user : users) {
            TreeSet<Division> divisions = new TreeSet<>();
            for(Season season: user.getSeasons().stream().filter(hs->!hs.isChallenge()).collect(Collectors.toList())) {
                divisions.add(season.getDivision());
            }
            for (Division division : divisions) {
                Stat s = new Stat();
                s.setUser(user);
                switch (division) {
                    case EIGHT_BALL_THURSDAYS:
                        s.setType(StatType.LIFETIME_EIGHT_BALL_THURSDAY);
                        Stat.calculate(user,s,
                                results.parallelStream().filter(r->r.getSeason().getDivision() == Division.EIGHT_BALL_THURSDAYS).collect(Collectors.toList()));
                        lifeDivisionStats.add(s);
                        break;
                    case EIGHT_BALL_WEDNESDAYS:
                        s.setType(StatType.LIFETIME_EIGHT_BALL_WEDNESDAY);
                        Stat.calculate(user,s,
                                results.parallelStream().filter(r->r.getSeason().getDivision() == Division.EIGHT_BALL_WEDNESDAYS).collect(Collectors.toList()));
                        lifeDivisionStats.add(s);
                        break;
                    case NINE_BALL_TUESDAYS:
                        s.setType(StatType.LIFETIME_NINE_BALL_TUESDAY);
                        Stat.calculate(user,s,
                                results.parallelStream().filter(r->r.getSeason().getDivision() == Division.NINE_BALL_TUESDAYS).collect(Collectors.toList()));
                        lifeDivisionStats.add(s);
                        break;
                    case MIXED_MONDAYS_MIXED:
                        s = new Stat();
                        s.setUser(user);
                        s.setType(StatType.LIFETIME_EIGHT_BALL_SCRAMBLE);
                        Stat.calculate(user,s,
                                results.parallelStream().filter(r->r.getTeamMatch().getDivision() == Division.MIXED_EIGHT).collect(Collectors.toList()));
                        lifeDivisionStats.add(s);

                        s = new Stat();
                        s.setUser(user);
                        s.setType(StatType.LIFETIME_NINE_BALL_SCRAMBLE);
                        Stat.calculate(user,s,
                                results.parallelStream().filter(r->r.getTeamMatch().getDivision() == Division.MIXED_NINE).collect(Collectors.toList()));
                        lifeDivisionStats.add(s);
                        break;
                }
            }
        }
        lifetimeDivisionStats.lazySet(lifeDivisionStats);
    }

    private void refreshUserSeasonStats(Season season, Map<Season,List<Stat>> userSeasonStats) {

        List<PlayerResult> results = leagueService.findAll(PlayerResult.class).stream().parallel().
                filter(pr -> pr.getSeason().equals(season)).filter(PlayerResult::hasResults).
                collect(Collectors.toList());
        Map<User, List<PlayerResult>> losers = results.stream()
                .filter(r->r.getLoser() != null).collect(Collectors.groupingBy(PlayerResult::getLoser, Collectors.toList()));

        Map<User, List<PlayerResult>> winners = results.stream()
                .filter(r->r.getWinner() != null).collect(Collectors.groupingBy(PlayerResult::getWinner, Collectors.toList()));
        Map<User, List<PlayerResult>> all = new HashMap<>();
        List<User> users = leagueService.findAll(User.class);

        for (User user : users) {
            List<PlayerResult> userResults = results.parallelStream().filter(p->p.hasUser(user)).collect(Collectors.toList());
            all.put(user,userResults);
        }
        List<Stat> stats = new ArrayList<>(100);
        List<Team> teams = leagueService.findAll(Team.class).stream().filter(t->t.getSeason().equals(season)).collect(Collectors.toList());
        for (User user : all.keySet()) {
            if (season.isScramble())  {
                stats.add(buildSeasonStats(user,teams,season,
                        all.get(user).stream().filter(pr->pr.getTeamMatch().getDivision() == Division.MIXED_EIGHT).collect(Collectors.toList()),
                        StatType.MIXED_EIGHT));
                stats.add(buildSeasonStats(user,teams,season,
                        all.get(user).stream().filter(pr->pr.getTeamMatch().getDivision() == Division.MIXED_NINE).collect(Collectors.toList()),
                        StatType.MIXED_NINE));
            } else {
                stats.add(buildSeasonStats(user,teams,season,all.get(user),null));
            }

        }
        userSeasonStats.put(season,stats);
    }

    private Stat buildSeasonStats(User user, List<Team> teams, Season season, List<PlayerResult> results, StatType statType) {
        Stat s = Stat.buildPlayerSeasonStats(user,
                season,
                results
        );
        s.setSeason(season);
        s.setTeam(teams.stream().filter(t->t.hasUser(user)).findFirst().orElse(null));
        s.setHandicap(user.getHandicap(season));
        if (statType != null)
            s.setType(statType);

        return s;
    }

    private void refreshUserSeasonStats(boolean active) {
        Collection<Season> seasons = leagueService.findAll(Season.class);
        Map<Season,List<Stat>> userSeasonStats = new HashMap<>(1000);
        for (Season season : seasons) {
            refreshUserSeasonStats(season,userSeasonStats);
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
        Map<User,List<PlayerResult>> winners = leagueService.findCurrent(PlayerResult.class).stream().filter(r->r.getWinner() != null)
                .collect(Collectors.groupingBy(PlayerResult::getWinner));
        Map<User,List<PlayerResult>> loser =   leagueService.findCurrent(PlayerResult.class).stream().filter(r->r.getLoser() != null)
                .collect(Collectors.groupingBy(PlayerResult::getLoser));

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
                List<PlayerResult> l = loser.get(user).stream()
                        .filter(p -> p.getWinnerHandicap() == handicap)
                        .collect(Collectors.toList());
                if (l.isEmpty()) {
                    continue;
                }
                stats.add(Stat.buildHandicapStats(l, StatType.HANDICAP_LOSES, user, handicap));
            }
        }
        handicapStats.lazySet(stats);
    }

    public void refreshUserHandicapStats(final User user) {
         Map<User,List<PlayerResult>> winners = leagueService.findCurrent(PlayerResult.class).stream().filter(pr -> pr.hasUser(user)).
                collect(Collectors.groupingBy(PlayerResult::getWinner));
        Map<User,List<PlayerResult>> loser = leagueService.findCurrent(PlayerResult.class).stream().filter(pr -> pr.hasUser(user)).
                collect(Collectors.groupingBy(PlayerResult::getLoser));

        List<Stat> stats = new ArrayList<>(100);

        for (Handicap handicap : Handicap.values()) {
            List<PlayerResult> w = winners.get(user).stream().filter(p->p.getLoserHandicap() == handicap).collect(Collectors.toList());
            if (w.isEmpty()) {
                continue;
            }
            stats.add(Stat.buildHandicapStats(w,StatType.HANDICAP_WINS,user,handicap));
        }
        for (Handicap handicap : Handicap.values()) {
            List<PlayerResult> l = loser.get(user).stream()
                    .filter(p -> p.getWinnerHandicap() == handicap)
                    .collect(Collectors.toList());
            if (l.isEmpty()) {
                continue;
            }
            stats.add(Stat.buildHandicapStats(l, StatType.HANDICAP_LOSES, user, handicap));
        }
        for (Stat stat : handicapStats.get()) {
            for (Stat hcStats : stats) {
                if (stat.getUser().equals(hcStats.getUser())
                        && stat.getType() == hcStats.getType()
                        && stat.getHandicap().equals(hcStats.getHandicap())
                        ) {
                    stat.setWins(hcStats.getWins());
                    stat.setLoses(hcStats.getLoses());
                    stat.setRacksWon(hcStats.getRacksWon());
                    stat.setRacksLost(hcStats.getRacksLost());
                } else {
                    handicapStats.get().add(hcStats);
                }
            }
        }
    }

    public void refreshTeamMatchStats(final TeamMatch tm) {
        refreshTeamStats(tm.getHome());
        refreshTeamStats(tm.getAway());
        refreshTeamMatch(tm);
        refreshTeamRank();
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
                    if (t.isNine()) {
                        if (!Objects.equals(t1.getStats().getSetLoses(), t.getStats().getSetLoses())) {
                            return t.getStats().getSetLoses().compareTo(t1.getStats().getSetLoses());
                        }
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

    public void refreshUserSeasonStats(final User user) {
        for (Season season : user.getSeasons().stream().filter(s -> s.isActive()).collect(Collectors.toList())) {
            List<PlayerResult> results = leagueService.findAll(PlayerResult.class).stream().parallel().
                    filter(pr->pr.hasUser(user))
                    .filter(pr -> pr.getSeason().equals(season)).
                    collect(Collectors.toList());

            Stat s = Stat.buildPlayerSeasonStats(user,
                        season,
                        results
                );
            Stat userStat = userSeasonStat.get().get(season).stream().parallel().filter(st->st.getUser().equals(user)).findFirst().orElse(null);
            if (userStat == null) {
                userSeasonStat.get().get(season).add(s);
            } else {
                userStat.setWins(s.getWins());
                userStat.setLoses(s.getLoses());
                userStat.setRacksWon(s.getRacksWon());
                userStat.setRacksLost(s.getRacksLost());
            }
            List<Stat> stats = userSeasonStat.get().get(season).stream().sorted(new Comparator<Stat>() {
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
            }).collect(Collectors.toList());

            int rank = 0;
            for (Stat stat : stats) {
                stat.setRank(++rank);
            }
            userSeasonStat.get().put(season,stats);
        }


    }

    public void refreshPlayerResult(final PlayerResult pr) {
        if (threadPoolTaskExecutor.getActiveCount() > 1) {
            logger.info("Skipping player refresh");
            return;
        }
        logger.info("Submitting to task for player refresh");
         threadPoolTaskExecutor.submit(new Runnable() {
             @Override
             public void run() {
                 refreshTeamMatch(pr.getTeamMatch());
                 refreshUserHandicapStats(pr.getPlayerHome());
                 refreshUserHandicapStats(pr.getPlayerAway());
                 refreshUserSeasonStats(pr.getPlayerHome());
                 refreshUserSeasonStats(pr.getPlayerAway());
             }
         });
    }

    public void refreshTeamMatch(TeamMatch tm) {
        if (tm.isChallenge() || tm.isNine()) {
            return ;
        }
        long start = System.currentTimeMillis();
        logger.info("Starting Time Refresh Match ");
        List<PlayerResult> results = leagueService.findCurrent(PlayerResult.class).parallelStream().filter(p->p.getTeamMatch().equals(tm)).collect(Collectors.toList());
        if (results.isEmpty())
            return;
        int homeWins = 0;
        int awayWins = 0;

        for(int i = 0 ; i < results.size(); i++) {
            PlayerResult result = results.get(i);
            if (result.getHomeRacks() > result.getAwayRacks()) {
                homeWins++;
            }
            if (result.getHomeRacks() < result.getAwayRacks()) {
                awayWins++;
            }
        }

        if (tm.getHomeRacks() > tm.getAwayRacks()) {
            if (tm.getHomeRacks()  != homeWins + tm.getHomeForfeits()) {
                tm.setHandicapRacks(tm.getHomeRacks() - homeWins - tm.getHomeForfeits());
            }
        } else {
            if (tm.getAwayRacks()  != awayWins + tm.getAwayForfeits()) {
                tm.setHandicapRacks(tm.getAwayRacks() - awayWins - tm.getAwayForfeits());
            }
        }
        logger.info("End TeamMatch Refresh "  + (System.currentTimeMillis() - start));
    }

    public void refreshTeamStats(Team team) {
        final Set<TeamMatch>  teamMatches = leagueService.findCurrent(TeamMatch.class);
        Stat stat =  Stat.buildTeamStats(team,
                teamMatches.parallelStream()
                        .filter(tm -> tm.hasTeam(team))
                        .filter(TeamMatch::isHasResults)
                        .collect(Collectors.toList())
        );

        if (Stat.isDifferent(stat, team.getStats())) {
            team.setStats(stat);
            leagueService.save(team);
        }

    }

    public void setEnableRefresh(boolean enableRefresh) {
        this.enableRefresh = enableRefresh;
    }
}
