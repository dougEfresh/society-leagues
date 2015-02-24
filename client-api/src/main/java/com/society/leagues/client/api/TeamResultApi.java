package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.TeamResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public interface TeamResultApi extends ClientApi<TeamResult> {

    @Override
    @RequestMapping(value = "/api/client/teamresult/{id}" , method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    TeamResult get(@PathVariable(value = "id") Integer id);

    @Override
    @RequestMapping(value = "/api/client/teamresult/list" , method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    List<TeamResult> get();

    @RequestMapping(value = "/api/client/teamresult/team" , method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    List<TeamResult> getByTeam(@RequestBody Team team);
}
