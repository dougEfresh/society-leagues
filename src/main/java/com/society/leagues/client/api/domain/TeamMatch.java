package com.society.leagues.client.api.domain;


import com.society.leagues.client.api.domain.division.Division;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TeamMatch extends LeagueObject  {
    @NotNull Team home;
    @NotNull Team away;
    @NotNull Season season;
    LocalDateTime matchDate;
    TeamResult result;


    public TeamMatch(Team home, Team away, Season season, LocalDateTime matchDate) {
        this.home = home;
        this.away = away;
        this.season = season;
        this.matchDate = matchDate;
    }

    public TeamMatch() {
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

    public LocalDateTime getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDateTime matchDate) {
        this.matchDate = matchDate;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public Division getDivision() {
        return this.season.getDivision();
    }

    public TeamResult getResult() {
        return result;
    }

    public void setResult(TeamResult result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "TeamMatch{" +
                "home=" + home +
                ", away=" + away +
                ", matchDate=" + matchDate +
                '}';
    }
}
