package com.society.leagues.client.api.domain;


import javax.validation.constraints.NotNull;
import java.util.Date;

public class Match extends LeagueObject  {
    @NotNull
    Team home;
    @NotNull
    Team away;
    @NotNull
    Season season;
    @NotNull
    Date matchDate;
    Integer racks;
    Integer win;

    public Match(Team home, Team away, Season season, Date matchDate) {
        this.home = home;
        this.away = away;
        this.season = season;
        this.matchDate = matchDate;
    }

    @Override
    public int compareTo(LeagueObject o) {
        //return this.matchDate.compareTo(o.matchDate);
        return super.compareTo(o);
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

    public Integer getRacks() {
        return racks;
    }

    public void setRacks(Integer racks) {
        this.racks = racks;
    }

    public Integer getWin() {
        return win;
    }

    public void setWin(Integer win) {
        this.win = win;
    }

    public Team getWinner() {
        if (home.getId().equals(win)) {
            return home;
        }
        if (away.getId().equals(win)) {
            return away;
        }
        return null;
    }
}
