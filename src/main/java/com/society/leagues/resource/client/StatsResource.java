package com.society.leagues.resource.client;

import com.society.leagues.WebListCache;
import com.society.leagues.WebMapCache;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
@SuppressWarnings("unused")
public class StatsResource {

    @Autowired UserDao dao;
    @Autowired PlayerDao playerDao;
    @Autowired PlayerResultDao playerResultDao;
    @Autowired ChallengeDao challengeDao;
    @Autowired TeamResultDao teamResultDao;
    @Autowired WebMapCache<Map<Integer,UserStats>> cache;
    @Autowired JdbcTemplate jdbcTemplate;

    List<Map<String,Object>> all;
    List<Map<String,Object>> season;
    List<Map<String,Object>> divisions;
    List<Map<String,Object>> challenge;

    @RequestMapping(value = "/stats", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Integer,UserStats> getStats() {
        if (!cache.isEmpty()) {
            return cache.getCache();
        }
        all = jdbcTemplate.queryForList("select * from user_stats_all_vw");
        season = jdbcTemplate.queryForList("select * from user_stats_season_vw");
        divisions = jdbcTemplate.queryForList("select * from user_stats_division_vw");
        challenge = jdbcTemplate.queryForList("select * from user_stats_challenge_vw");

        Map<Integer,UserStats>  stats = new HashMap<>();
        for (User user : dao.get()) {
            stats.put(user.getId(),getUserStats(user));
        }
        return stats;
    }

    @RequestMapping(value = "/stats/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserStats getStats(@PathVariable Integer userId) {
        return getUserStats(dao.get(userId));
    }

    public UserStats getUserStats(User user) {
        UserStats userStats = new UserStats();
        userStats.setAll(all.stream().filter(u -> u.get("user_id").equals(user.getId())).findFirst().orElse(null));
        userStats.setSeason(season.stream().filter(u->u.get("user_id").equals(user.getId())).collect(Collectors.toList()));
        userStats.setDivision(divisions.stream().filter(u->u.get("user_id").equals(user.getId())).collect(Collectors.toList()));
        userStats.setChallenge(challenge.stream().filter(u->u.get("user_id").equals(user.getId())).collect(Collectors.toList()));
        return userStats;
    }

    public List<PlayerStats> playerStats(User user,Handicap handicap) {
        List<PlayerStats> playerStats = new ArrayList<>();
        for (Player player : playerDao.getByUser(user)) {
            playerStats.add(getStats(player,handicap));
        }
        return playerStats;
    }

    public List<PlayerStats> playerStats(User user) {
        List<PlayerStats> playerStats = new ArrayList<>();
        for (Player player : playerDao.getByUser(user)) {
            playerStats.add(getStats(player,null));
        }
        return playerStats;
    }

    public PlayerStats getStats(Player player, Handicap handicap) {

        List<PlayerResult> results =  null;
        if (handicap == null) {
           results = playerResultDao.get().stream().filter(p ->
                            p.getPlayerAway().equals(player) ||
                                    p.getPlayerHome().equals(player)
            ).collect(Collectors.toList());
        }
        else {
            results =  playerResultDao.get().stream().
                    filter(p -> p.getPlayerAway().equals(player) ||
                                    p.getPlayerHome().equals(player)).
                    filter(p -> p.getPlayerHome().getHandicap() == handicap)
                    .collect(Collectors.toList());
        }

        PlayerStats playerStats = new PlayerStats();
        playerStats.setPlayer(player);
        for (PlayerResult result : results) {
            if (result.getHomeRacks() > result.getAwayRacks()) {
                if (result.getPlayerHome().equals(player)){
                    playerStats.addPoints(3);
                    playerStats.addWin(result.getHomeRacks(),result.getAwayRacks());
                } else {
                    playerStats.addPoints(1);
                    playerStats.addLost(result.getAwayRacks(), result.getHomeRacks());
                }
            } else {
                if (result.getPlayerHome().equals(player)) {
                    playerStats.addPoints(1);
                    playerStats.addLost(result.getHomeRacks(),result.getAwayRacks());
                } else {
                    playerStats.addPoints(3);
                    playerStats.addWin(result.getAwayRacks(),result.getHomeRacks());
                }
            }
        }
        return playerStats;
    }

}
