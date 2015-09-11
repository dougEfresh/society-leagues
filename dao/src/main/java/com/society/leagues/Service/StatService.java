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
    final AtomicReference<List<Stat>> seasonStats = new AtomicReference<>(new ArrayList<>(1000));
    final AtomicReference<List<Stat>> handicapStats = new AtomicReference<>(new ArrayList<>(2000));

    @Autowired LeagueService leagueService;
    @PostConstruct
    public void init() {
        refresh();
    }

    public List<Stat> getSeasonStats() {
        return seasonStats.get();
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
                    leagueService.findTeamMatchByTeam(team).parallelStream().filter(tm->tm.hasResults()).collect(Collectors.toList())
            ));
        }
        this.teamStats.lazySet(teamStats);
        /*
        Map<User,List<PlayerResult>> homeResults = results.parallelStream().collect(Collectors.groupingBy(PlayerResult::getPlayerHome));
        Map<User,List<PlayerResult>> awayResults = results.parallelStream().collect(Collectors.groupingBy(PlayerResult::getPlayerAway));
        Map<User,List<PlayerResult>> allResults = new HashMap<User,List<PlayerResult>>(5000);
        for (User user : homeResults.keySet()) {
            if (!allResults.containsKey(user)) {
                allResults.put(user,new ArrayList<>(100));
            }
            allResults.get(user).addAll(homeResults.get(user));
        }

        for (User user : awayResults.keySet()) {
            if (!allResults.containsKey(user)) {
                allResults.put(user,new ArrayList<>(100));
            }
            allResults.get(user).addAll(awayResults.get(user));
        }

        */
        refreshUserStats();
        logger.info("Done Refreshing stats  (" + (System.currentTimeMillis()-start) + "ms)");
    }

    private void refreshUserStats() {
        logger.info("Refreshing User stats");
        List<User> users = leagueService.findAll(User.class);
        List<PlayerResult> playerResults = leagueService.findAll(PlayerResult.class);
        List<Stat> newStats = new ArrayList<>(1000);
        for (User user : users) {
            List<PlayerResult> userResults = playerResults.parallelStream().filter(r->r.hasUser(user)).collect(Collectors.toList());
            List<Stat> userStats = new ArrayList<>();
            for (HandicapSeason hs : user.getHandicapSeasons()) {
                Season s = hs.getSeason();
                Stat stat = Stat.buildPlayerSeasonStats(user, s,
                        userResults.stream().parallel().
                                filter(r -> r.getSeason().equals(s)).
                                filter(r -> r.hasUser(user)).
                                collect(Collectors.toList())
                );
                userStats.add(stat);
            }
            newStats.addAll(userStats);
            newStats.add(Stat.buildLifeTimeStats(user, userStats));
        }
        seasonStats.lazySet(newStats);
        logger.info("Created " + seasonStats.get().size() + " stats for " + users.size() + " users with a total of " + playerResults.size() );
    }
}
