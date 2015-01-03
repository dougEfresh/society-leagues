package com.society.leagues.client.admin.api;

import com.society.leagues.client.api.domain.PlayerMatch;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path(value = "/api/admin/match")
@Consumes(MediaType.APPLICATION_JSON)
public interface MatchResultApi {

    @Path(value = "save")
    @POST
    Integer save(PlayerMatch matchResult);

    @Path(value = "delete/{id}")
    @GET
    Boolean delete(@PathParam(value = "id")Integer id);

    @Path(value = "get/{id}")
    @GET
    PlayerMatch get(@PathParam(value = "id")Integer id);

}
