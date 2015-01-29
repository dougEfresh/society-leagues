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
    @Path("/api/client/user/current")
    @POST
    List<User> current(List<User> users);

    @Override
    @Path("/api/client/user/current/{id}")
    @GET
    List<User> current(@PathParam(value = "id") Integer userId);

    @Override
    @Path("/api/client/user/past")
    @POST
    List<User> past(List<User> user);

    @Override
    @Path("/api/client/user/past/{id}")
    @GET
    List<User> past(@PathParam(value = "id") Integer userId);

    @Override
    @Path("/api/client/user/all")
    @POST
    List<User> all(List<User> user);

    @Override
    @Path("/api/client/user/all/{id}")
    @GET
    List<User> all(@PathParam(value = "id") Integer userId);

    @Override
    @Path(value = "/api/client/user/get")
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

