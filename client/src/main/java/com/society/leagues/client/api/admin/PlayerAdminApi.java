package com.society.leagues.client.api.admin;

import com.society.leagues.client.api.domain.Player;

public interface PlayerAdminApi {

    Player create(Player player);

    Boolean delete(Player player);

    Player modify(Player player);

}