package com.society.leagues.client.api.domain.division;

import com.society.leagues.client.api.domain.LeagueObject;
import com.society.leagues.client.api.domain.league.League;

public class Division extends LeagueObject {

    DivisionType type;
    League league;
    Integer id;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
