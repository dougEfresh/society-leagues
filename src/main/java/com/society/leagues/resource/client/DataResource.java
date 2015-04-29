package com.society.leagues.resource.client;

import com.society.leagues.adapters.PlayerResultAdapter;
import com.society.leagues.adapters.SeasonAdapter;
import com.society.leagues.adapters.TeamMatchAdapter;
import com.society.leagues.adapters.TeamResultAdapter;
import com.society.leagues.client.api.domain.TeamResult;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.dao.DivisionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping(value = "/api")
@RestController
public class DataResource {
    @Autowired DivisionResource divisionResource;
    @Autowired PlayerResource playerResource;
    @Autowired SeasonResource seasonResource;
    @Autowired UserResource userResource;
    @Autowired TeamResource teamResource;
    @Autowired MatchResource matchResource;
    @Autowired ResultResource resultResource;
    @Autowired DivisionDao divisionDao;

    @RequestMapping(value = "/data", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> data() {
        Map<String,Object> data = new HashMap<>();
        data.put("divisions",divisionResource.divisions());
        data.put("seasons",seasonResource.getSeasons());
        data.put("players",playerResource.players());
        data.put("teams",teamResource.teams());
        data.put("users",userResource.get());

        Map<Integer,List<TeamMatchAdapter>> currentMatches = new HashMap<>();

        List<SeasonAdapter> season = seasonResource.getSeasons().values().stream().
                filter(s -> s.current()).
                collect(Collectors.toList());
        List<SeasonAdapter> currentSeasons =  new ArrayList<>();

        //Exclude Challenge
        for (SeasonAdapter currentSeason : season) {
            Division d = divisionDao.get(currentSeason.getDivision());
            if (!d.isChallenge()) {
                currentSeasons.add(currentSeason);
            }
        }

        for (SeasonAdapter currentSeason : currentSeasons) {
            List<TeamMatchAdapter> matchAdapters = matchResource.getTeamMatches(currentSeason.getSeason().getId());
            currentMatches.put(currentSeason.getSeason().getId(),matchAdapters);
        }
        data.put("currentTeamMatches",currentMatches);

        Map<Integer,List<TeamResultAdapter>> currentTeamResults = new HashMap<>();
        for (SeasonAdapter currentSeason : currentSeasons) {
            List<TeamResultAdapter> adapters = resultResource.getTeamResults(currentSeason.getSeason().getId());
            currentTeamResults.put(currentSeason.getSeason().getId(),adapters);
        }
        data.put("currentTeamResults",currentTeamResults);

        Map<Integer,List<PlayerResultAdapter>> currentPlayerResults = new HashMap<>();
        for (SeasonAdapter currentSeason : currentSeasons) {
            currentPlayerResults.put(currentSeason.getSeason().getId(),resultResource.getPlayerResults(currentSeason.getSeason().getId()));
        }

        data.put("currentPlayerResults",currentPlayerResults);

        return data;
    }
}
