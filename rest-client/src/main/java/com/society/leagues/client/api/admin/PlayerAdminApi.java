package com.society.leagues.client.api.admin;

import com.society.leagues.client.api.domain.Player;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path(value = "/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface PlayerAdminApi {

    @Path(value = "api/admin/player/create")
    @POST
    Player create(Player player);

    @Path(value = "api/admin/player/delete")
    @POST
    Boolean delete(Player player);

    @Path(value = "api/admin/player/modify")
    @POST
    Player modify(Player player);

}
