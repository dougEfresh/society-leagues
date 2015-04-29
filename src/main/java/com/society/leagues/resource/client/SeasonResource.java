package com.society.leagues.resource.client;

import com.society.leagues.adapters.SeasonAdapter;
import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.division.DivisionType;
import com.society.leagues.dao.DivisionDao;
import com.society.leagues.dao.PlayerDao;
import com.society.leagues.dao.SeasonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@RequestMapping(value = "/api")
@RestController
public class SeasonResource {
    @Autowired SeasonDao seasonDao;
    @Autowired PlayerDao playerDao;
    @Autowired DivisionDao divisionDao;
    @Autowired JdbcTemplate jdbcTemplate;

    @RequestMapping(value = "/seasons", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Integer,SeasonAdapter>  getSeasons() {
        Map<Integer,SeasonAdapter> seasons = new HashMap<>();
        for(Player player : playerDao.get()) {
            if (!seasons.containsKey(player.getSeason().getId())) {
                SeasonAdapter seasonAdapter = new SeasonAdapter(player.getSeason(),player.getDivision());
                seasons.put(player.getSeason().getId(), seasonAdapter);
            }
            seasons.get(player.getSeason().getId()).addPlayer(player);
            seasons.get(player.getSeason().getId()).addTeam(player.getTeam());
        }
        return seasons;
    }
}
