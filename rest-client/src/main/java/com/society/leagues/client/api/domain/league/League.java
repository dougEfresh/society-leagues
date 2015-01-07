package com.society.leagues.client.api.domain.league;

import com.society.leagues.client.api.domain.LeagueObject;

import javax.validation.constraints.NotNull;

public class League extends LeagueObject {
    @NotNull
    LeagueType type;

    public League(LeagueType type) {
        this.type = type;
    }

    public League() {
    }

    public LeagueType getType() {
        return type;
    }

    public void setType(LeagueType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "League{" +
                "type=" + type +
                ", id=" + id +
                '}';
    }
}
