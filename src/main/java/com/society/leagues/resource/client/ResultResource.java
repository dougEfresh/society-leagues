package com.society.leagues.resource.client;

import com.society.leagues.WebMapCache;
import com.society.leagues.adapters.PlayerResultAdapter;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
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
     Map<Integer,Map<Integer,List<PlayerResultAdapter>>> pastResultCache = Collections.emptyMap();
    private static Logger logger = LoggerFactory.getLogger(DataResource.class);

    @PostConstruct
    public void init() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info("Getting past results");
                pastResultCache = getResults(false);
                logger.info("Done getting past results");
            }
        });
        t.setDaemon(true);
        t.start();
    }

    @RequestMapping(value = "/results/current/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Integer,Map<Integer,List<PlayerResultAdapter>>> getPlayerResults() {
        if (!currentResultCache.isEmpty()) {
            return currentResultCache.getCache();
        }
        currentResultCache.setCache(getResults(true));
        return currentResultCache.getCache();
    }

    @RequestMapping(value = "/results/past/users/{seasonId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Integer,List<PlayerResultAdapter>> getPlayerPastResults(@PathVariable Integer seasonId) {
        return pastResultCache.get(seasonId);
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
