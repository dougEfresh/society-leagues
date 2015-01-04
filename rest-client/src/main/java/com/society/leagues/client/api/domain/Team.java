package com.society.leagues.client.api.domain;

import com.society.leagues.client.api.domain.league.League;

public class Team {
    String name;
    League league;
    Integer id;
    Integer captain;

    public Team(String name, League league) {
        this.name = name;
        this.league = league;
    }

    public Team(String name, League league, Integer captain) {
        this.name = name;
        this.league = league;
        this.captain = captain;
    }

    public Team() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getCaptain() {
        return captain;
    }

    public void setCaptain(Integer captain) {
        this.captain = captain;
    }

    @Override
    public String toString() {
        return "Team{" +
                "name='" + name + '\'' +
                ", league=" + league +
                ", id=" + id +
                ", captain=" + captain +
                '}';
    }
}
