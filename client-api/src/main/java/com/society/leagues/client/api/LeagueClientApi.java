package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.league.League;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path(value = "/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface LeagueClientApi {


    @Path(value = "api/client/season/list")
    @GET
    List<League> list();

    
    @Path(value = "api/client/season/{id}")
    @GET
    League get(@PathParam(value = "id") Integer id);
}

