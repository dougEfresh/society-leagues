package com.society.leagues.resource.client;

import com.society.leagues.WebMapCache;
import com.society.leagues.adapters.PlayerResultAdapter;
import com.society.leagues.adapters.PlayerResultRawAdapter;
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
    List<PlayerResultRawAdapter> pastResults = Collections.emptyList();
    private static Logger logger = LoggerFactory.getLogger(DataResource.class);

    @PostConstruct
    public void init() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info("Getting past results");
                //pastResultCache = getResults(false);
                logger.info("Done getting past results");
            }
        });
        t.setDaemon(true);
        t.start();
    }

    @RequestMapping(value = "/results/current", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlayerResultRawAdapter> getCurrentResults() {
        return getPlayerResults(true);
    }

    @RequestMapping(value = "/results/past", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlayerResultRawAdapter> getPastResults() {
        if (pastResults.isEmpty()) {
            pastResults = getPlayerResults(false);
        }
        return pastResults;
    }

    @RequestMapping(value = "/results/past/season/{seasonId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlayerResultRawAdapter> getPastResults(@PathVariable Integer seasonId) {
        if (pastResults.isEmpty()) {
            pastResults = getPlayerResults(false);
        }
        return pastResults.stream().filter(p->p.getSeasonId().equals(seasonId)).collect(Collectors.toList());
    }

    private List<PlayerResultRawAdapter> getPlayerResults(boolean active) {
        /**
         * Only grab challenge league results
         */
        List<PlayerResult> results;
        if (active) {
            results = playerResultDao.get().stream().
                    filter(p->p.getTeamMatch().getSeason().getSeasonStatus() == Status.ACTIVE).
                    filter(p->p.getTeamMatch().getSeason().getDivision().isChallenge())
                    .collect(Collectors.toList());
        } else {
            results = playerResultDao.get().stream().filter(p->p.getTeamMatch().getSeason().getSeasonStatus() != Status.ACTIVE)
                    .collect(Collectors.toList());
        }
        List<PlayerResultRawAdapter> resultsAdapter = new ArrayList<>(results.size());
        for (PlayerResult playerResult : results) {
            if (playerResult.getPlayerAway().getUser().getName().contains("FORFEIT") ||
                    playerResult.getPlayerAway().getUser().getName().contains("BYE") ||
                    playerResult.getPlayerAway().getUser().getName().contains("HANDICAP")
                    ) {
                continue;
            }

            if (playerResult.getPlayerHome().getUser().getName().contains("FORFEIT") ||
                    playerResult.getPlayerHome().getUser().getName().contains("BYE") ||
                    playerResult.getPlayerHome().getUser().getName().contains("HANDICAP")
                    ) {
                continue;
            }
            resultsAdapter.add(new PlayerResultRawAdapter(playerResult));
        }
        return resultsAdapter;
    }

}
