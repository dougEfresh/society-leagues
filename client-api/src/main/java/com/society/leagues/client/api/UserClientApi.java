package com.society.leagues.client.api;

import com.society.leagues.client.api.admin.UserAdminApi;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.User;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import java.util.List;


@Path(value = "/")
@Produces("application/json")
@Consumes("application/json")
@RolesAllowed(value = {"ADMIN","User"})
public interface UserClientApi extends ClientApi<User> {
    
    @Override
    @Path(value = "/api/client/users")
    @GET
    List<User> get();

    @Override
    @GET
    @Path(value = "/api/client/user/get/{id}")
    User get(@PathParam(value = "id") Integer id);
    
    @Path(value = "/api/client/user/login/{login}")
    @GET
    User get(@PathParam(value = "login") String login);

}

