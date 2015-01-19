package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.User;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

public interface PlayerClientApi extends ClientApi<Player> {

    @Override
    @Path("/api/client/player/current")
    List<Player> current(List<User> users);

    @Override
    @Path("/api/client/player/current/{id}")
    List<Player> current(Integer userId);

    @Override
    @Path("/api/client/player/all")
    List<Player> past(List<User> user);

    @Override
    @Path("/api/client/player/past/{id}")
    List<Player> past(Integer userId);

    @Override
    @Path("/api/client/player/all")
    List<Player> all(List<User> user);

    @Override
    @Path("/api/client/player/all/{id}")
    List<Player> all(Integer userId);
}
