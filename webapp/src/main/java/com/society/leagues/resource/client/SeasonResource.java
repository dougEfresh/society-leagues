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
    public List<SeasonAdapter> getSeasons() {
        Collection<Season> activeSeason = seasonDao.get();
        return activeSeason.stream().map(SeasonAdapter::new).collect(Collectors.toList());
    }
}
