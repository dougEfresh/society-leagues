package com.society.leagues.resource.client;

import com.society.leagues.WebMapCache;
import com.society.leagues.dao.DivisionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;


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
    @Autowired StatsResource statsResource;
    @Autowired WebMapCache<Map<String,Object>> dataCache;

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
        data.put("results",resultResource.getPlayerResults());
        data.put("stats",statsResource.getStats());
        data.put("teamStats",statsResource.getTeamStats());
        dataCache.setCache(data);
        return data;
    }
}
