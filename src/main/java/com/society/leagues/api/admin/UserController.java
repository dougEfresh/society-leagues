package com.society.leagues.api.admin;

import com.society.leagues.api.ApiController;
import com.society.leagues.domain.DomainUser;
import com.society.leagues.domain.annotation.CurrentlyLoggedInUser;
import com.society.leagues.domain.interfaces.Player;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api( value = "/admin/user" ,
        description = "Player Management & Info",
        basePath = "/api/vi"
)
@RequestMapping("/api/v1/admin/user")
@RestController
@PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
public class UserController extends ApiController {

    @RequestMapping(value = "/create",method = RequestMethod.POST)
    @ApiOperation(value = "/create" , notes = "Create a user")
    public Integer create(@ApiParam(access = "internal")
                              @CurrentlyLoggedInUser
                              DomainUser user,
                          @RequestParam String login,
                          @RequestParam String fname,
                          @RequestParam String lname,
                          @RequestParam String email,
                          @RequestParam String password) {
        return 10;
    }
}