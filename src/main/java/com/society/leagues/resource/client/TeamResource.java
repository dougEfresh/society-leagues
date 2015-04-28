package com.society.leagues.resource.client;

import com.society.leagues.client.api.domain.Team;
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
public class TeamResource {

    @Autowired TeamDao teamDao;

    @RequestMapping(value = "/teams", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Integer,Team> teams() {
        Map<Integer,Team> teams = new HashMap<>();
        for (Team team: teamDao.get()) {
            teams.put(team.getId(),team);
        }
        return teams;
    }
}
