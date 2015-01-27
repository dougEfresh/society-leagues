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
    @POST @Path("/api/client/match/current")
    List<Match> current(List<User> users);

    @Override
    @GET @Path("/api/client/match/current/{id}")
    List<Match> current(@PathParam(value = "id") Integer userId);

    @Override
    @POST @Path("/api/client/match/past")
    List<Match> past(List<User> user);

    @Override
    @GET @Path("/api/client/match/past/{id}")
    List<Match> past(@PathParam(value = "id") Integer userId);

    @Override
    @POST @Path("/api/client/match/all")
    List<Match> all(List<User> user);

    @Override
    @GET @Path("/api/client/match/all/{id}")
    List<Match> all(@PathParam(value = "id") Integer userId);

    @Override
    @GET @Path("/api/client/match/get")
    List<Match> get();

}
