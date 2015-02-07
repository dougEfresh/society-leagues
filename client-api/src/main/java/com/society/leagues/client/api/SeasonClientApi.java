package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path(value = "/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface SeasonClientApi extends ClientApi<Season> {

    @Override
    @GET
    @Path(value = "/api/client/season")
    List<Season> get();

    @Override
    @GET
    @Path("/api/client/season/id/{id}")
    Season get(@PathParam(value = "id")Integer id);

    @GET
    @Path("/api/client/season/name/{name}")
    Season get(@PathParam(value = "name")String name);
}
