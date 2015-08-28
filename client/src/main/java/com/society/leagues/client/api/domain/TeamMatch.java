package com.society.leagues.client.api.domain;


import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class TeamMatch extends LeagueObject {
    @NotNull
    Team home;
    @NotNull
    Team away;
    @NotNull @DBRef
    Season season;
    LocalDateTime matchDate;

    Integer homeRacks = -1;
    Integer awayRacks = -1;

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

    public Integer getAwayRacks() {
        return awayRacks;
    }

    public void setAwayRacks(Integer awayRacks) {
        this.awayRacks = awayRacks;
    }

    public Integer getHomeRacks() {
        return homeRacks;
    }

    public void setHomeRacks(Integer homeRacks) {
        this.homeRacks = homeRacks;
    }

    @Override
    public String toString() {
        return "TeamMatch{" +
                "id="+ id +
                ", home=" + home +
                ", away=" + away +
                ", matchDate=" + matchDate +
                '}';
    }
}
