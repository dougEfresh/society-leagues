package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.TokenResponse;
import com.society.leagues.client.api.domain.User;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(value = "/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/api/auth", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON,description = "")
public interface AuthApi {

    @Path(value = "authenticate")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Authenticate with username and password", response = TokenResponse.class)
    TokenResponse authenticate(User user);
}
