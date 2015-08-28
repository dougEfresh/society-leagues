package com.society.leagues.resource.client;

import com.society.leagues.WebMapCache;
import com.society.leagues.adapters.UserAdapter;
import com.society.leagues.dao.DivisionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequestMapping(value = "/api")
@RestController
public class DataResource {
    @Autowired DivisionResource divisionResource;
    @Autowired SeasonResource seasonResource;
    @Autowired UserResource userResource;
    @Autowired TeamResource teamResource;
    @Autowired MatchResource matchResource;
    @Autowired ResultResource resultResource;
    @Autowired DivisionDao divisionDao;
    @Autowired StatsResource statsResource;
    @Autowired ChallengeResource challengeResource;
    private static Logger logger = LoggerFactory.getLogger(DataResource.class);

    @RequestMapping(value = "/data", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> data() {
        Map<String,Object> data = new HashMap<>();
        logger.info("Getting divisions");
        data.put("divisions",divisionResource.divisions());
        logger.info("Getting seasons");
        data.put("seasons",seasonResource.getSeasons());
        logger.info("Getting teams");
        data.put("teams",teamResource.teams());
        logger.info("Getting users");
        data.put("users",userResource.get());
        logger.info("Getting user results");
        data.put("userResults",resultResource.getCurrentResults());
        logger.info("Getting team results");
        data.put("teamResults",matchResource.getTeamMatchesCurrent());
        logger.info("Getting user stats");
        data.put("userStats",statsResource.getStats());
        logger.info("Getting team stats");
        data.put("teamStats", statsResource.getTeamStats());
        logger.info("Getting slots");
        data.put("slots",challengeResource.getSlots());
        data.put("challenges",challengeResource.getCurrentChallenges());

        return data;
    }
}
