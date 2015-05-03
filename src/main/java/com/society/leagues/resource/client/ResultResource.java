package com.society.leagues.resource.client;

import com.society.leagues.WebMapCache;
import com.society.leagues.adapters.PlayerResultAdapter;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
    @Autowired WebMapCache<Map<Integer,Map<Integer,List<PlayerResultAdapter>>>> cache;

    @RequestMapping(value = "/results/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Integer,Map<Integer,List<PlayerResultAdapter>>> getPlayerResults() {
        if (!cache.isEmpty()) {
            return cache.getCache();
        }
        List<PlayerResult> results = playerResultDao.get().stream().filter(r -> r.getTeamMatch().getSeason().getSeasonStatus() == Status.ACTIVE).collect(Collectors.toList());
        Map<Integer,Map<Integer,List<PlayerResultAdapter>>> seasonResults = new HashMap<>();
        Set<User> users = new HashSet<>();
        for (PlayerResult result : results) {
            users.add(result.getPlayerAway().getUser());
            users.add(result.getPlayerHome().getUser());
        }

        for (Season season : seasonDao.getActive()) {
            Map<Integer,List<PlayerResultAdapter>> userResults = new HashMap<>();
            seasonResults.put(season.getId(),userResults);

            for (User user : users) {
                List<PlayerResult> userPlayerResults = results.stream().filter(
                        p -> p.getPlayerAway().getUser().equals(user) ||
                                p.getPlayerHome().getUser().equals(user)
                ).filter(p ->p.getPlayerHome().getSeason().equals(season))
                        .collect(Collectors.toList());
                if (userPlayerResults.isEmpty()) {
                    continue;
                }
                List<PlayerResultAdapter> playerResultAdapters = userPlayerResults.stream().map(userPlayerResult -> new PlayerResultAdapter(user, userPlayerResult)).collect(Collectors.toList());
                userResults.put(user.getId(), playerResultAdapters);
            }
        }
        cache.setCache(seasonResults);
        return cache.getCache();
    }
}
