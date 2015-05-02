package com.society.leagues.resource.client;

import com.society.leagues.adapters.PlayerResultAdapter;
import com.society.leagues.adapters.TeamMatchAdapter;
import com.society.leagues.adapters.TeamResultAdapter;
import com.society.leagues.adapters.UserResultAdapter;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
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
    @Autowired SeasonDao seasonDao;

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
            adapter.add(new TeamResultAdapter(result,new TeamMatchAdapter(result.getTeamMatch(),null)));
        }

        return adapter;
    }


    @RequestMapping(value = "/results/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Integer,Map<Integer,UserResultAdapter>> getPlayerResults() {
        List<PlayerResult> results = playerResultDao.get().stream().filter(r -> r.getTeamMatch().getSeason().getSeasonStatus() == Status.ACTIVE).collect(Collectors.toList());
        Map<Integer,Map<Integer,UserResultAdapter>> userResults = new HashMap<>();
        Set<User> users = new HashSet<>();
        for (PlayerResult result : results) {
            users.add(result.getPlayerAway().getUser());
            users.add(result.getPlayerHome().getUser());
        }

        for (User user : users) {
            Map<Integer,UserResultAdapter> seasonResults = new HashMap<>();
            for (Season season : seasonDao.getActive()) {
                List<PlayerResult> userPlayerResults = results.stream().filter(
                        p -> p.getPlayerAway().getUser().equals(user) ||
                                p.getPlayerHome().getUser().equals(user)
                ).filter(p ->p.getPlayerHome().getSeason().equals(season))
                        .collect(Collectors.toList());
                if (userPlayerResults.isEmpty()) {
                    continue;
                }
                List<PlayerResultAdapter> playerResultAdapters = new ArrayList<>();
                for (PlayerResult userPlayerResult : userPlayerResults) {
                    playerResultAdapters.add(new PlayerResultAdapter(user, userPlayerResult));
                }
                seasonResults.put(season.getId(), new UserResultAdapter(user,playerResultAdapters));
                userResults.put(user.getId(), seasonResults);
            }
        }
        return userResults;
    }

}
