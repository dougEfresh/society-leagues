package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path(value = "/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface TeamClientApi extends ClientApi<Team> {

    @Override
    @Path("/api/client/team/current")
    @POST
    List<Team> current(List<User> users);

    @Override
    @Path("/api/client/team/current/{id}")
    @GET
    List<Team> current(@PathParam(value = "id")Integer userId);

    @Override
    @Path("/api/client/team/past")
    @POST
    List<Team> past(List<User> user);

    @Override
    @Path("/api/client/team/past/{id}")
    @GET
    List<Team> past(@PathParam(value = "id")Integer userId);

    @Override
    @Path("/api/client/team/all")
    @POST
    List<Team> all(List<User> user);

    @Override
    @Path("/api/client/team/all/{id}")
    @GET
    List<Team> all(@PathParam(value = "id")Integer userId);

    @Override
    @Path("/api/client/team/get/{id}")
    @GET
    Team get(@PathParam(value = "id") Integer id);
}
