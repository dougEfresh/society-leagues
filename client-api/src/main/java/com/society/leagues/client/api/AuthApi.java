package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.TokenResponse;
import com.society.leagues.client.api.domain.User;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
