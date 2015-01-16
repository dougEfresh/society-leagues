package com.society.leagues.client.api.domain;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

public class Player extends LeagueObject {
    @NotNull
    Season season;
    @NotNull
    User user;
    @NotNull
    Team team;
    @NotBlank
    String handicap;

    public Player(Season season, User user, Team team, String handicap) {
        this.season = season;
        this.user = user;
        this.team = team;
        this.handicap = handicap;
    }

    public Player() {
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getHandicap() {
        return handicap;
    }

    public void setHandicap(String handicap) {
        this.handicap = handicap;
    }
}
