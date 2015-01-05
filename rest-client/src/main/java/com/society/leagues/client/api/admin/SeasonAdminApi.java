package com.society.leagues.client.api.admin;

import com.society.leagues.client.api.domain.Season;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(value = "/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface SeasonAdminApi {

    @Path("api/admin/season/create")
    @POST
    Season create(final Season season);

    @Path("api/admin/season/delete")
    @POST
    Boolean delete(final Season season);

    @Path("api/admin/season/modify")
    @POST
    Season modify(Season season);
}
