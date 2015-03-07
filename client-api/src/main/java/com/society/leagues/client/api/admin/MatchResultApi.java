package com.society.leagues.client.api.admin;

import com.society.leagues.client.api.domain.PlayerMatch;

import javax.ws.rs.PathParam;

public interface MatchResultApi {

    Integer save(PlayerMatch matchResult);

    Boolean delete(@PathParam(value = "id")Integer id);

    PlayerMatch get(@PathParam(value = "id")Integer id);

}
