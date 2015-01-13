package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Season;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path(value = "/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface SeasonClientApi {
    
    @Path(value = "/api/client/season/{id}")
    @GET
    Season get(@PathParam(value = "id") Integer id);
}
