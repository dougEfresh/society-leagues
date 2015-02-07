package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Match;
import com.society.leagues.client.api.domain.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path(value = "/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface MatchApi extends ClientApi<Match> {

    @GET @Path(value = "/api/client/match/get/{id}")
    Match get(@PathParam(value = "id") Integer id);

    @Override
    @GET @Path("/api/client/match/get")
    List<Match> get();

}
