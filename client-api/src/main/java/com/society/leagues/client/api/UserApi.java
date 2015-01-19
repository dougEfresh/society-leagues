package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.User;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import java.util.List;


@Path(value = "/")
@Produces("application/json")
@Consumes("application/json")
@RolesAllowed(value = {"ADMIN","PLAYER"})
public interface UserApi {

    @Path("/api/user/info/{id}")
    @GET
    User info(@PathParam(value = "id") Integer id);
    

}

