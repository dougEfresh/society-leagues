package com.society.leagues.client.api.domain;

import org.springframework.data.mongodb.core.mapping.DBRef;

public class TeamSeason extends LeagueObject {

    @DBRef Season season;
    @DBRef Team team;

    public TeamSeason(Season season, Team team) {
        this.season = season;
        this.team = team;
    }

    public TeamSeason() {
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
