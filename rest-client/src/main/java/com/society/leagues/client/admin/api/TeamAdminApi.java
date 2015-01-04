package com.society.leagues.client.admin.api;


import com.society.leagues.client.api.domain.Team;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(value = "/api/admin/team")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface TeamAdminApi {

    @Path(value = "create")
    @POST
    Team create(Team team);

    @Path(value = "delete")
    @POST
    Boolean delete(Team team);
}
