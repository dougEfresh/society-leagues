package com.society.leagues.resource.client;

import com.society.leagues.WebMapCache;
import com.society.leagues.adapters.SeasonAdapter;
import com.society.leagues.adapters.TeamMatchAdapter;
import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Status;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.client.api.domain.division.DivisionType;
import com.society.leagues.dao.DivisionDao;
import com.society.leagues.dao.PlayerDao;
import com.society.leagues.dao.SeasonDao;
import com.society.leagues.dao.TeamMatchDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RequestMapping(value = "/api")
@RestController
public class SeasonResource {
    @Autowired SeasonDao seasonDao;
    @Autowired PlayerDao playerDao;
    @Autowired DivisionDao divisionDao;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MatchResource matchResource;
    @Autowired TeamMatchDao teamMatchDao;
    @Autowired WebMapCache<Map<Integer,SeasonAdapter>> pastSeasonCache;

    @RequestMapping(value = "/seasons", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Integer,SeasonAdapter> getSeasons() {
        Map<Integer,SeasonAdapter> seasons = new HashMap<>();
        Collection<Season> activeSeason = seasonDao.get();
        for (Season season : activeSeason) {
            SeasonAdapter seasonAdapter = new SeasonAdapter(
                        season,
                        matchResource.getTeamMatches(season.getId())
            );
            seasons.put(season.getId(), seasonAdapter);
        }
        return seasons;
    }

     @RequestMapping(value = "/seasons/current", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Integer,SeasonAdapter> getSeasonsCurrent() {
        Map<Integer,SeasonAdapter> seasons = new HashMap<>();
         Collection<Season> activeSeason = seasonDao.get().stream().filter(s->s.getSeasonStatus() == Status.ACTIVE).collect(Collectors.toList());
        for (Season season : activeSeason) {
            SeasonAdapter seasonAdapter = new SeasonAdapter(
                        season,
                        matchResource.getTeamMatches(season.getId())
            );
            seasons.put(season.getId(), seasonAdapter);
        }
        return seasons;
    }

    @RequestMapping(value = "/seasons/past", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Integer,SeasonAdapter> getSeasonsPast() {
        if (!pastSeasonCache.isEmpty()) {
            return pastSeasonCache.getCache();
        }

        Map<Integer,SeasonAdapter> seasons = new HashMap<>();
        Collection<Season> pastSeasons = seasonDao.get().stream().filter(s->s.getSeasonStatus() != Status.ACTIVE).collect(Collectors.toList());
        for (Season season : pastSeasons) {
            SeasonAdapter seasonAdapter = new SeasonAdapter(
                        season,
                        matchResource.getTeamMatches(season.getId())
            );
            seasons.put(season.getId(), seasonAdapter);
        }
        pastSeasonCache.setCache(seasons);
        return pastSeasonCache.getCache();
    }
}
