package com.society.leagues.controller;

import com.society.leagues.api.domain.User;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Api( value = "/admin/user" ,
        description = "Player Management & Info",
        basePath = "/api/vi"
)
@Path("/api/admin/user")
public class UserController extends ApiController {

    @Path(value = "/create")
    @POST
    @ApiOperation(value = "/create" , notes = "Create a user")
    public Integer create(@ApiParam User user) {
        return 10;
    }
}