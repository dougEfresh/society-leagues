package com.society.leagues.client.api.admin;

import com.society.leagues.client.api.domain.TeamMatch;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path(value = "/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface MatchAdminApi {

    @Path("api/admin/match/create")
    @POST
    TeamMatch create(TeamMatch teamMatch);

    @Path("api/admin/match/modify")
    @POST
    TeamMatch modify(TeamMatch teamMatch);
    
}
