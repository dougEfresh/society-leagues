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
        data.put("teams",teamResource.teams());
        data.put("users",userResource.get());
        data.put("results",resultResource.getPlayerResults());
        return data;
    }
}
