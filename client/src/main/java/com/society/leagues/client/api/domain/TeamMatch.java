package com.society.leagues.client.api.domain;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.society.leagues.client.api.domain.converters.DateTimeDeSerializer;
import com.society.leagues.client.api.domain.converters.DateTimeSerializer;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class TeamMatch extends LeagueObject {
    @NotNull Team home;
    @NotNull Team away;
    @DBRef Season season;

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeSerializer.class)
    LocalDateTime matchDate;

    Integer homeRacks = -1;
    Integer awayRacks = -1;

    public TeamMatch(Team home, Team away, LocalDateTime matchDate) {
        this.home = home;
        this.away = away;
        this.matchDate = matchDate;
        this.season = home.getSeason();
    }

    public TeamMatch() {
    }

    public TeamMatch(String id) {
        this.id = id;
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

    public Division getDivision() {
        return getSeason().getDivision();
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
