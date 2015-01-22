package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path(value = "/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface SeasonClientApi extends ClientApi<Season> {

    @Override
    @Path("/api/client/season/current")
    @POST
    List<Season> current(List<User> users);

    @Override
    @Path("/api/client/season/current/{id}")
    @GET
    List<Season> current(@PathParam(value = "id")Integer userId);

    @Override
    @Path("/api/client/season/past")
    @POST
    List<Season> past(List<User> user);

    @Override
    @Path("/api/client/season/past/{id}")
    @GET
    List<Season> past(@PathParam(value = "id")Integer userId);

    @Override
    @Path("/api/client/season/all")
    @POST
    List<Season> all(List<User> user);

    @Override
    @Path("/api/client/season/all/{id}")
    @GET
    List<Season> all(@PathParam(value = "id")Integer userId);

    @Override
    @GET
    @Path("/api/client/season/get/{id}")
    Season get(@PathParam(value = "id")Integer id);
}
