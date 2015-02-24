package com.society.leagues.client.api.admin;

import com.society.leagues.client.api.domain.TeamResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface TeamResultAdminApi {

    @RequestMapping(value = "/api/v1/team/result/create",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    TeamResult create(@RequestBody TeamResult teamResult);

    @RequestMapping(value = "/api/v1/team/result/modify",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    TeamResult modify(@RequestBody TeamResult teamResult);

}
