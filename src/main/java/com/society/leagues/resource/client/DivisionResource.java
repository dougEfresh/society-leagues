package com.society.leagues.resource.client;

import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.dao.DivisionDao;
import com.society.leagues.dao.TeamDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping(value = "/api")
@RestController
public class DivisionResource {

    @Autowired DivisionDao divisionDao;

    @RequestMapping(value = "/divisions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Integer,Division> teams() {
        Map<Integer,Division> divisions = new HashMap<>();
        for (Division d: divisionDao.get()) {
            divisions.put(d.getId(),d);
        }
        return divisions;
    }
}
