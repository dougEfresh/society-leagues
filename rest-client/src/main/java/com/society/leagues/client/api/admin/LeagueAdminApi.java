package com.society.leagues.client.api.admin;

import com.society.leagues.client.api.domain.league.League;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path(value = "/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface LeagueAdminApi {

    @Path("api/admin/create")
    @POST
    League create(final League league);

    @Path("api/admin/delete")
    @POST
    Boolean delete(final League league);

    @Path("api/admin/modify")
    @POST
    League modify(League league);
}
