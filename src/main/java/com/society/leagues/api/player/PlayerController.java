package com.society.leagues.api.player;

import com.society.leagues.api.ApiController;
import com.society.leagues.domain.annotation.CurrentlyLoggedInUser;
import com.society.leagues.domain.DomainUser;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api( value = "/player" ,
      description = "Player Management & Info",
      basePath = "/api/vi"
)
@RequestMapping("/api/v1/player")
@RestController
@PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
public class PlayerController extends ApiController {
    @Autowired PlayerDao dao;

    @RequestMapping(value = "/teamHistory",method = RequestMethod.POST)
    @ApiOperation(value = "/teamHistory" , notes = "Get players stats for all teams")
    public Map<String,Object> teamHistory(@ApiParam(access = "internal") @CurrentlyLoggedInUser DomainUser user) {
        return dao.getTeamHistory(user.getId());
    }
}
