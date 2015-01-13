package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.division.Division;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path(value = "/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface DivisionClientApi {

    @Path(value = "api/client/division/list")
    @GET
    List<Division> list();

    
    @Path(value = "api/client/division/{id}")
    @GET
    Division get(@PathParam(value = "id") Integer id);
}
