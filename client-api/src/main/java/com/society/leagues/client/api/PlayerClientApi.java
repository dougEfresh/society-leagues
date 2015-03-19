package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Player;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path(value = "/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface PlayerClientApi extends ClientApi<Player> {


    @Override
    @Path("/api/client/player/get/{id}")
    @GET
    Player get(@PathParam(value = "id") Integer id);

}
