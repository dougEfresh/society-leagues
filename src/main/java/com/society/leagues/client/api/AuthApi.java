package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.User;
import com.society.leagues.infrastructure.token.TokenResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path(value = "/api/auth")
@Produces(MediaType.APPLICATION_JSON)
public interface AuthApi {

    @Path(value = "authenticate")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    TokenResponse authenticate(User user);
}
