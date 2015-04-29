package com.society.leagues.resource.client;

import com.society.leagues.adapters.PlayerResultAdapter;
import com.society.leagues.adapters.TeamMatchAdapter;
import com.society.leagues.adapters.TeamResultAdapter;
import com.society.leagues.client.api.domain.PlayerResult;
import com.society.leagues.client.api.domain.TeamResult;
import com.society.leagues.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
@SuppressWarnings("unused")
public class ResultResource {
    @Autowired UserDao dao;
    @Autowired PlayerDao playerDao;
    @Autowired PlayerResultDao playerResultDao;
    @Autowired ChallengeDao challengeDao;
    @Autowired TeamResultDao teamResultDao;


    @RequestMapping(value = "/result/teams/{seasonId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TeamResultAdapter> getTeamResults(@PathVariable Integer seasonId) {
        List<TeamResultAdapter> adapter = new ArrayList<>(100);
        List<TeamResult> results = teamResultDao.get().stream().filter(tr-> tr.getTeamMatch().getSeason().getId().equals(seasonId)).sorted(new Comparator<TeamResult>() {
            @Override
            public int compare(TeamResult o1, TeamResult o2) {
                return o1.getTeamMatch().getMatchDate().compareTo(o2.getTeamMatch().getMatchDate());
            }
        }).collect(Collectors.toList());

        for (TeamResult result : results) {
            adapter.add(new TeamResultAdapter(result,new TeamMatchAdapter(result.getTeamMatch())));
        }

        return adapter;
    }


    @RequestMapping(value = "/result/players/{seasonId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlayerResultAdapter> getPlayerResults(@PathVariable Integer seasonId) {
        List<PlayerResult> results = playerResultDao.get().stream().filter(r->r.getTeamMatch().getSeason().getId().equals(seasonId)).collect(Collectors.toList());
        List<PlayerResultAdapter> playerResultAdapters = new ArrayList<>(results.size());
        playerResultAdapters.addAll(results.stream().map(PlayerResultAdapter::new).collect(Collectors.toList()));
        return playerResultAdapters;
    }

}
