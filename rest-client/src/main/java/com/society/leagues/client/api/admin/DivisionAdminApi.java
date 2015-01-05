package com.society.leagues.client.api.admin;

import com.society.leagues.client.api.domain.division.Division;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(value = "/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface DivisionAdminApi {

    @Path("api/admin/division/create")
    @POST
    Division create(final Division division);

    @Path("api/admin/division/delete")
    @POST
    Boolean delete(final Division division);

    @Path("api/admin/division/modify")
    @POST
    Division modify(Division division);
}
