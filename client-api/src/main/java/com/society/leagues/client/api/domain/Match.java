package com.society.leagues.client.api.domain;

import java.time.LocalDate;

public class Match extends LeagueObject {
    Team home;
    Team away;
    Season season;
    LocalDate matchDate;

    public Match(Team home, Team away, Season season, LocalDate matchDate) {
        this.home = home;
        this.away = away;
        this.season = season;
        this.matchDate = matchDate;
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

    public LocalDate getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDate matchDate) {
        this.matchDate = matchDate;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }
}
