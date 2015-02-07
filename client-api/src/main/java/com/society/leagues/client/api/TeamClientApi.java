package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path(value = "/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface TeamClientApi extends ClientApi<Team>  {

    @Path(value = "/api/client/team/name/{name}")
    @GET
    Team get(@PathParam(value = "name") String name);

    @Override
    @Path("/api/client/team/")
    @GET
    List<Team> get();
}
