package com.society.leagues.resource.client;

import com.society.leagues.WebMapCache;
import com.society.leagues.adapters.TeamStatsSeasonAdapter;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static Logger logger = LoggerFactory.getLogger(DataResource.class);

    @Autowired UserDao dao;
    @Autowired PlayerDao playerDao;
    @Autowired PlayerResultDao playerResultDao;
    @Autowired ChallengeDao challengeDao;
    @Autowired TeamResultDao teamResultDao;
    @Autowired WebMapCache<Map<Integer,UserStats>> cache;
    @Autowired WebMapCache<Map<Integer,List<TeamStatsSeasonAdapter>>> statsTeamCache;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired SeasonDao seasonDao;

    List<Map<String,Object>> all;
    List<Map<String,Object>> season;
    List<Map<String,Object>> divisions;
    List<Map<String,Object>> challenge;
    List<Map<String,Object>> handicapAll;
    List<Map<String,Object>> handicapDivision;
    List<Map<String,Object>> handicapSeason;
    List<Map<String,Object>> handicapChallenge;

    @RequestMapping(value = "/stats/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Integer,UserStats> getStats() {
        logger.info("Getting all stats");
        all = jdbcTemplate.queryForList("select user_id as userId,matches,wins,loses,racks_for as racksFor, racks_against as racksAgainst from user_stats_all_vw");
        logger.info("Getting season stats");
        season = jdbcTemplate.queryForList("select user_id as userId,season_id as seasonId,matches,wins,loses,racks_for as racksFor, racks_against as racksAgainst from user_stats_season_vw");
        ///logger.info("Getting division stats");
        //divisions = jdbcTemplate.queryForList("select user_id as userId,division_id as divisionId,matches,wins,loses,racks_for as racksFor, racks_against as racksAgainst from user_stats_division_vw");
        //logger.info("Getting challenge stats");
        //challenge = jdbcTemplate.queryForList("select user_id as userId,wins*3+loses as points, matches,wins,loses,racks_for as racksFor, racks_against as racksAgainst from user_stats_challenge_vw");
        //logger.info("Getting handicap stats");
        //handicapAll = jdbcTemplate.queryForList("select user_id as userId, opponent_handicap as handicap, matches,wins,loses,racks_for as racksFor, racks_against as racksAgainst from user_stats_handicap_all_vw");
        //handicapSeason = jdbcTemplate.queryForList("select user_id as userId, opponent_handicap as handicap, season_id as seasonId, matches,wins,loses,racks_for as racksFor, racks_against as racksAgainst from user_stats_handicap_season_vw");
        //handicapDivision = jdbcTemplate.queryForList("select user_id as userId, opponent_handicap as handicap, division_id as divisionId, matches,wins,loses,racks_for as racksFor, racks_against as racksAgainst from user_stats_handicap_division_vw");
        //handicapChallenge = jdbcTemplate.queryForList("select user_id as userId, opponent_handicap as handicap, matches,wins,loses,racks_for as racksFor, racks_against as racksAgainst from user_stats_handicap_challenge_vw");

        //handicapSeason = Collections.emptyList();
        //handicapDivision = Collections.emptyList();
        //handicapChallenge = Collections.emptyList();

        Map<Integer,UserStats>  stats = new HashMap<>();

        for (User user : dao.get()) {
            stats.put(user.getId(),getUserStats(user));
        }
//        cache.setCache(stats);
        return stats;
    }

    @RequestMapping(value = "/stats/team", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Integer,List<TeamStatsSeasonAdapter>> getTeamStats() {
        if (!statsTeamCache.isEmpty()) {
            return statsTeamCache.getCache();
        }
        List<Map<String,Object>> stats  = jdbcTemplate.queryForList("select a.*,b.setWins,b.setLoses from  " +
                        "team_stats_vw a join team_set_vw b " +
                        "on a.team_id=b.team_id and a.season_id=b.season_id " +
                        "order by season_id,wins DESC,setWins DESC, racks_for DESC"
        );
        Map<Integer,List<TeamStatsSeasonAdapter>> cache = new HashMap<>();
        for (Map<String, Object> stat : stats) {
            Integer seasonId = (Integer) stat.get("season_id");
            if (!cache.containsKey(seasonId)) {
                cache.put(seasonId, new ArrayList<>());
            }
            cache.get(seasonId).add(new TeamStatsSeasonAdapter(stat));
        }
        statsTeamCache.setCache(cache);
        return statsTeamCache.getCache();
    }

     public UserStats getUserStats(User user) {
         UserStats userStats = new UserStats();
         userStats.setAll(all.stream().filter(u -> u.get("userId").equals(user.getId())).findFirst().orElse(null));
         userStats.setSeason(season.stream().filter(u->u.get("userId").equals(user.getId())).collect(Collectors.toList()));
         //userStats.setDivision(divisions.stream().filter(u->u.get("userId").equals(user.getId())).collect(Collectors.toList()));
         //userStats.setChallenge(challenge.stream().filter(u->u.get("userId").equals(user.getId())).collect(Collectors.toList()));
         //userStats.setHandicapAll(handicapAll.stream().filter(u->u.get("userId").equals(user.getId())).collect(Collectors.toList()));
         //userStats.setHandicapSeason(handicapSeason.stream().filter(u->u.get("userId").equals(user.getId())).collect(Collectors.toList()));
         //userStats.setHandicapDivision(handicapDivision.stream().filter(u->u.get("userId").equals(user.getId())).collect(Collectors.toList()));
         //userStats.setHandicapChallenge(handicapChallenge.stream().filter(u->u.get("userId").equals(user.getId())).collect(Collectors.toList()));
         return userStats;
    }

}
