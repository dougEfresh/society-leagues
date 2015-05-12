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
    @Autowired WebMapCache<Map<String,Object>> dataCache;
    private static Logger logger = LoggerFactory.getLogger(DataResource.class);

    @PostConstruct
    public void init() {
        //Load the cache up
        logger.info("Pre-Caching data");
        data();
        logger.info("Finished pre-Caching data");
    }

    @RequestMapping(value = "/data", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> data() {
        if (!dataCache.isEmpty()) {
            return dataCache.getCache();
        }
        Map<String,Object> data = new HashMap<>();
        data.put("divisions",divisionResource.divisions());
        data.put("seasons",seasonResource.getSeasons());
        data.put("teams",teamResource.teams());
        data.put("users",userResource.get());
        data.put("userResults",resultResource.getCurrentResults());
        data.put("teamResults",matchResource.getTeamMatchesCurrent());
        data.put("stats",statsResource.getStats());
        data.put("teamStats", statsResource.getTeamStats());
        dataCache.setCache(data);
        return data;
    }
}
