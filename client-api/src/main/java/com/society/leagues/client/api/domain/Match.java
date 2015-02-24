package com.society.leagues.client.api.domain;


import com.society.leagues.client.api.domain.division.Division;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class Match extends LeagueObject  {
    @NotNull Team home;
    @NotNull Team away;
    @NotNull Season season;
    @NotNull Division division;
    Date matchDate;

    public Match(Team home, Team away, Season season, Date matchDate) {
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

    public Date getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(Date matchDate) {
        this.matchDate = matchDate;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }
}
