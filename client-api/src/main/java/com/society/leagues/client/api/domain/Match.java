package com.society.leagues.client.api.domain;


import javax.validation.constraints.NotNull;
import java.util.Date;

public class Match extends LeagueObject implements Comparable<Match> {
    @NotNull
    Team home;
    @NotNull
    Team away;
    @NotNull
    Season season;
    @NotNull
    Date matchDate;

    public Match(Team home, Team away, Season season, Date matchDate) {
        this.home = home;
        this.away = away;
        this.season = season;
        this.matchDate = matchDate;
    }

    @Override
    public int compareTo(Match o) {
        return this.matchDate.compareTo(o.matchDate);
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
}
