package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Login;
import com.society.leagues.client.api.domain.TokenResponse;
import com.society.leagues.client.api.domain.User;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(value = "/api/v1/auth")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/api/v1/auth", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON, description = "Authentication Token")
public interface AuthApi {

    @Path(value = "authenticate")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequestMapping(value = "/api/v1/auth/authenticate", method = RequestMethod.POST, produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Authenticate with username and password", response = TokenResponse.class)
    TokenResponse authenticate(Login user);
}
