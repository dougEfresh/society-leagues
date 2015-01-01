package com.society.leagues.controller;

import com.wordnik.swagger.annotations.*;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path(value = "/auth")
@Api(value = "/auth",
        description = "Login to get token",
        position = 1,
        produces = "application/json")
public class AuthController extends ApiController {

    @Path(value = "/login")
    @ApiOperation(value = "login",
            notes = "These fields can also be in the Header or Cookie of the request",
            response = String.class)
    public String authenticate(@ApiParam(required = true, defaultValue = "email_608@domain.com")
                               @HeaderParam(value = "X-Auth-Username")
                               @QueryParam(value = "X-Auth-Username")
                               String username,
                               @ApiParam(required = true, defaultValue = "password_608")
                               @QueryParam(value = "X-Auth-Password")
                               @HeaderParam(value = "X-Auth-Password")
                               String password) {
        return "Should never be hit";
    }
}
