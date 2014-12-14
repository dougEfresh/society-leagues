package com.society.leagues.api.player;

import com.society.leagues.domain.BaseController;
import com.society.leagues.domain.annotation.CurrentlyLoggedInUser;
import com.society.leagues.domain.DomainUser;
import com.society.leagues.domain.annotation.RequiresAuth;
import com.society.leagues.domain.annotation.RestAuthController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RequestMapping("/api/v1/player")
@RestAuthController
public class PlayerController extends BaseController {
    @Autowired PlayerDao dao;

    @RequestMapping(value = "/teamHistory",method = RequestMethod.POST)
    public Map<String,Object> teamHistory(@CurrentlyLoggedInUser DomainUser user) {
        return dao.getTeamHistory(user.getId());
    }
}
