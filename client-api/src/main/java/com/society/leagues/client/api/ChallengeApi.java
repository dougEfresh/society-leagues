package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Player;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path(value = "/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ChallengeApi {
    
    @Path("/api/challenge/potential/{id}")
    @GET
    List<Player> getPotentials(@PathParam(value = "id") Integer id);
}
