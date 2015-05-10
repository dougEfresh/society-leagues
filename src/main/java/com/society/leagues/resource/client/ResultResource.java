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
    @Autowired WebMapCache<Map<Integer,Map<Integer,List<PlayerResultAdapter>>>> currentResultCache;
    @Autowired WebMapCache<Map<Integer,Map<Integer,List<PlayerResultAdapter>>>> pastResultCache;

    @RequestMapping(value = "/results/current/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Integer,Map<Integer,List<PlayerResultAdapter>>> getPlayerResults() {
        if (!currentResultCache.isEmpty()) {
            return currentResultCache.getCache();
        }
        currentResultCache.setCache(getResults(true));
        return currentResultCache.getCache();
    }

    @RequestMapping(value = "/results/past/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Integer,Map<Integer,List<PlayerResultAdapter>>> getPlayerPastResults() {
           if (!pastResultCache.isEmpty()) {
               return pastResultCache.getCache();
           }
        pastResultCache.setCache(getResults(false));
        return pastResultCache.getCache();
    }

    private Map<Integer,Map<Integer,List<PlayerResultAdapter>>> getResults(boolean current) {
        Collection<PlayerResult> results = playerResultDao.get();
        Map<Integer,Map<Integer,List<PlayerResultAdapter>>> seasonResults = new HashMap<>();
        Set<User> users = new HashSet<>();
        for (PlayerResult result : results) {
            users.add(result.getPlayerAway().getUser());
            users.add(result.getPlayerHome().getUser());
        }
        List<Season> seasons = new ArrayList<>();
        if (current) {
            seasons = seasonDao.getActive();
        } else {
            seasons = seasonDao.getInActive();
        }
        for (Season season : seasons) {
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

        return seasonResults;
    }

}
