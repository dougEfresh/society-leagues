package com.society.leagues.client.api.admin;


import com.society.leagues.client.api.domain.Team;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(value = "/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface TeamAdminApi {

    @Path(value = "/api/admin/team/create")
    @POST
    Team create(Team team);

    @Path(value = "/api/admin/team/delete")
    @POST
    Boolean delete(Team team);
}
