package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.division.Division;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path(value = "/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface DivisionClientApi {

    @Path(value = "api/division/list")
    @GET
    List<Division> list();

}
