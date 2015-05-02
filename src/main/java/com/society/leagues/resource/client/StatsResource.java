package com.society.leagues.resource.client;

import com.society.leagues.WebListCache;
import com.society.leagues.WebMapCache;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    @Autowired WebListCache<List<Map<String,Object>>> statsTeamCache;
    @Autowired JdbcTemplate jdbcTemplate;

    List<Map<String,Object>> all;
    List<Map<String,Object>> season;
    List<Map<String,Object>> divisions;
    List<Map<String,Object>> challenge;

    @RequestMapping(value = "/stats/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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
        cache.setCache(stats);
        return stats;
    }

    @RequestMapping(value = "/stats/team", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String,Object>> getTeamStats() {
        if (!statsTeamCache.isEmpty()) {
            return statsTeamCache.getCache();
        }

        statsTeamCache.setCache(jdbcTemplate.queryForList("select * from  team_stats_vw order by season_id,wins DESC ,racks_for DESC"));
        return statsTeamCache.getCache();
    }

     public UserStats getUserStats(User user) {
        UserStats userStats = new UserStats();
        userStats.setAll(all.stream().filter(u -> u.get("user_id").equals(user.getId())).findFirst().orElse(null));
        userStats.setSeason(season.stream().filter(u->u.get("user_id").equals(user.getId())).collect(Collectors.toList()));
        userStats.setDivision(divisions.stream().filter(u->u.get("user_id").equals(user.getId())).collect(Collectors.toList()));
        userStats.setChallenge(challenge.stream().filter(u->u.get("user_id").equals(user.getId())).collect(Collectors.toList()));
        return userStats;
    }

}
