package com.society.leagues.client.admin.api;

import com.society.leagues.client.api.domain.league.League;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path(value = "/api/admin/league")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface LeagueAdminApi {

    @Path("create")
    @POST
    League create(final League league);

    @Path("delete/{id}")
    @GET
    Boolean delete(@PathParam(value = "id") Integer id);

    @Path("modify")
    @POST
    League modify(League league);
}
