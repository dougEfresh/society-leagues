package com.society.leagues.client.api.admin;

import com.society.leagues.client.api.domain.PlayerMatch;

public interface MatchResultApi {

    Integer save(PlayerMatch matchResult);

    Boolean delete(Integer id);

    PlayerMatch get(Integer id);

}
