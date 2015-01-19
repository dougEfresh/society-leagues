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
    @Path("/api/client/season/current")
    List<Season> current(List<User> users);

    @Override
    @Path("/api/client/season/current/{id}")
    List<Season> current(Integer userId);

    @Override
    @Path("/api/client/season/past")
    List<Season> past(List<User> user);

    @Override
    @Path("/api/client/season/past/{id}")
    List<Season> past(Integer userId);

    @Override
    @Path("/api/client/season/all")
    List<Season> all(List<User> user);

    @Override
    @Path("/api/client/season/all/{id}")
    List<Season> all(Integer userId);
    
    
}
