package com.society.leagues.api.auth;

import com.society.leagues.api.ApiController;
import com.wordnik.swagger.annotations.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Api(value = "/auth",
        description = "Login to get token",
        position = 1,
        produces = "application/json")
public class AuthController extends ApiController {

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ApiOperation(value = "login",
            notes = "These fields can also be in the Header or Cookie of the request",
            response = String.class)
    public String authenticate(@ApiParam(required = true, defaultValue = "email_608@domain.com")
                               @RequestParam(value = "X-Auth-Username" )
                               String username,
                               @ApiParam(required = true, defaultValue = "password_608")
                               @RequestParam(value = "X-Auth-Password" )
                               String password) {
        return "Should never be hit";
    }
}
