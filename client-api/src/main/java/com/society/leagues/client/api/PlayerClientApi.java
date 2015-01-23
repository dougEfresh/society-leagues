package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.client.api.domain.division.Division;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path(value = "/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface PlayerClientApi extends ClientApi<Player> {

    @Override
    @Path("/api/client/player/current")
    @POST
    List<Player> current(List<User> users);

    @Override
    @Path("/api/client/player/current/{id}")
    @GET
    List<Player> current(@PathParam(value = "id") Integer userId);

    @Override
    @Path("/api/client/player/past")
    @POST
    List<Player> past(List<User> user);

    @Override
    @Path("/api/client/player/past/{id}")
    @GET
    List<Player> past(@PathParam(value = "id") Integer userId);

    @Override
    @Path("/api/client/player/all")
    @POST
    List<Player> all(List<User> user);

    @Override
    @Path("/api/client/player/all/{id}")
    @GET
    List<Player> all(@PathParam(value = "id") Integer userId);

    @Override
    @Path("/api/client/player/get/{id}")
    @GET
    Player get(@PathParam(value = "id") Integer id);
    
    @Path("/api/client/player/handicap/{to}/{from}")
    @POST
    List<Player> findHandicapRange(Division division,
                                   @PathParam(value = "to") Integer to, 
                                   @PathParam(value = "from") Integer from);
}
