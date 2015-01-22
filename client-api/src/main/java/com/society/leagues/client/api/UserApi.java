package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.User;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import java.util.List;


@Path(value = "/")
@Produces("application/json")
@Consumes("application/json")
@RolesAllowed(value = {"ADMIN","User"})
public interface UserApi extends ClientApi<User> {
    
    @Override
    @Path("/api/client/user/current")
    List<User> current(List<User> users);

    @Override
    @Path("/api/client/user/current/{id}")
    List<User> current(@PathParam(value = "id") Integer userId);

    @Override
    @Path("/api/client/user/all")
    List<User> past(List<User> user);

    @Override
    @Path("/api/client/user/past/{id}")
    List<User> past(@PathParam(value = "id") Integer userId);

    @Override
    @Path("/api/client/user/all")
    List<User> all(List<User> user);

    @Override
    @Path("/api/client/user/all/{id}")
    List<User> all(@PathParam(value = "id") Integer userId);
}

