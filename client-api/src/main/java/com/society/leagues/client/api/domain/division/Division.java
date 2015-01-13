package com.society.leagues.client.api.domain.division;

import com.society.leagues.client.api.domain.LeagueObject;
import com.society.leagues.client.api.domain.league.League;

import javax.validation.constraints.NotNull;

public class Division extends LeagueObject {
    @NotNull
    DivisionType type;
    @NotNull
    League league;

    public Division(DivisionType type, League league) {
        this.type = type;
        this.league = league;
    }

    public Division() {
    }

    public DivisionType getType() {
        return type;
    }

    public void setType(DivisionType type) {
        this.type = type;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    @Override
    public String toString() {
        return "Division{" +
                "type=" + type +
                ", league=" + league +
                ", id=" + id +
                '}';
    }
}
