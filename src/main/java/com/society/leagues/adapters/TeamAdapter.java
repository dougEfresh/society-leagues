package com.society.leagues.adapters;

import com.society.leagues.client.api.domain.Team;

@SuppressWarnings("unused")
public class TeamAdapter {
    Team team;

    public TeamAdapter(Team team) {
        this.team = team;
    }

    public TeamAdapter() {
    }

    public Integer getId() {
        return team.getId();
    }
}
