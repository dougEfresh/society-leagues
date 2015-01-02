package com.society.leagues.resource;

import com.society.leagues.client.api.AuthApi;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.infrastructure.security.PrincipalToken;
import com.society.leagues.infrastructure.security.ServiceAuthenticator;
import com.society.leagues.infrastructure.token.TokenResponse;
import com.wordnik.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Api(value = "/auth",
        description = "Login to get token",
        position = 1,
        produces = "application/json")
@Component
public class AuthResource extends ApiResource implements AuthApi {
    @Autowired ServiceAuthenticator authenticator;


    @ApiOperation(value = "login",
            notes = "These fields can also be in the Header or Cookie of the request",
            response = String.class)
    @Path(value = "authenticate")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public TokenResponse authenticate (
            @ApiParam(required = true, defaultValue = "email_608@domain.com")
            @FormParam(value = "username") String username,
            @ApiParam(required = true, defaultValue = "password_608")
            @FormParam(value = "password") String password) {
        return auth(new User(username,password));
    }

    @Path(value = "authenticate")
    @POST
    @ApiOperation(value = "login",
            notes = "These fields can also be in the Header or Cookie of the request",
            response = String.class)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public TokenResponse authenticate(
            @ApiParam(required = true) User user) {
         return auth(user);
    }

    private TokenResponse auth(User user) {
        PrincipalToken principalToken = authenticator.authenticate(user.getUsername(),user.getPassword());
        return  new TokenResponse(principalToken.getToken());

    }
}
