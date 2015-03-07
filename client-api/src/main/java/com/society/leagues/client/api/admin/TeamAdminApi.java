package com.society.leagues.client.api.admin;


import com.society.leagues.client.api.domain.Team;

public interface TeamAdminApi {

    Team create(Team team);

    Boolean delete(Team team);
}
