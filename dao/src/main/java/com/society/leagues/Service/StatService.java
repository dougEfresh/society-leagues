package com.society.leagues.Service;

import com.society.leagues.client.api.domain.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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

    @Autowired LeagueService leagueService;

    @PostConstruct
    public void init() {
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

    @Scheduled(fixedRate = 1000*60*6, initialDelay = 1000*60*11)
    public void refresh() {
        logger.info("Refreshing stats");
        long start = System.currentTimeMillis();
        List<Team> teams = leagueService.findAll(Team.class);
        List<Stat> teamStats = new ArrayList<>();
        for (Team team : teams) {
            teamStats.add(Stat.buildTeamStats(team,
                    leagueService.findAll(TeamMatch.class).parallelStream()
                            .filter(tm -> tm.hasTeam(team))
                            .filter(TeamMatch::isHasResults)
                            .collect(Collectors.toList())
            ));
        }
        this.teamStats.lazySet(teamStats);
        refreshUserSeasonStats();
        refreshUserLifetimeStats();
        logger.info("Done Refreshing stats  (" + (System.currentTimeMillis()-start) + "ms)");

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
            for (User user : all.keySet()) {
                stats.add(Stat.buildPlayerSeasonStats(user,
                                season,
                                all.get(user)
                        )
                );
            }
            stats.stream().sorted((stat, t1) -> t1.getWinPct().compareTo(stat.getWinPct())).collect(Collectors.toList());
            userSeasonStats.put(season,stats);
        }
        this.userSeasonStat.lazySet(userSeasonStats);
    }

    private void refreshUserLifetimeStats() {
        logger.info("Refreshing User stats");
        List<User> users = leagueService.findAll(User.class);
        List<Stat> lifeStats = new ArrayList<>(1000);
        for (User user : users) {
            for (List<Stat> stats : this.userSeasonStat.get().values()) {
                lifeStats.add(Stat.buildLifeTimeStats(user,stats.stream().filter(s->s.getUser().equals(user)).collect(Collectors.toList())));
            }
        }
        lifetimeStats.lazySet(lifeStats);
        logger.info("Created " + lifetimeStats.get().size() + " stats for " + users.size() + " users with a total of " + lifeStats.size());
    }

}
