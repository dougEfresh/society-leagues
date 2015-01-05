package com.society.leagues.client.api.admin;

import com.society.leagues.client.api.domain.Player;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path(value = "/api/admin/player")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface PlayerAdminApi {

    @Path(value = "create")
    @POST
    Player create(Player player);

    @Path(value = "delete/{id}")
    @POST
    Boolean delete(@PathParam(value = "id") Integer id);

    @Path(value = "delete")
    @POST
    Player modify(Player player);

}
