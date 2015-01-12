package com.society.leagues.client.api.domain;

public class Match {
    Team home;
    Team away;
    Integer seasonId;

    public Match(Team home, Team away, Integer seasonId) {
        this.home = home;
        this.away = away;
        this.seasonId = seasonId;
    }

    public Match() {
    }

    public Team getHome() {
        return home;
    }

    public void setHome(Team home) {
        this.home = home;
    }

    public Team getAway() {
        return away;
    }

    public void setAway(Team away) {
        this.away = away;
    }
}
