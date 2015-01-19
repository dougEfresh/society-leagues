package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.LeagueObject;
import com.society.leagues.client.api.domain.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

public interface ClientApi<T extends LeagueObject> {
    
    @POST
    List<T> current(List<User> users);

    @GET
    List<T> current(@PathParam(value = "id")Integer userId);

    @POST
    List<T> past(List<User> user);

    @GET
    List<T> past(@PathParam(value = "id") Integer userId);
    
    @POST
    List<T> all(List<User> user);

    @GET
    List<T> all(@PathParam(value = "id") Integer userId);
    
    @GET
    T get(@PathParam(value = "id") Integer id);

}
