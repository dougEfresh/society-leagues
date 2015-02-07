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
    @Path("/api/client/player/get/{id}")
    @GET
    Player get(@PathParam(value = "id") Integer id);

}
