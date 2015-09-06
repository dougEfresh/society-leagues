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
    Integer setHomeWins = -1;
    Integer setAwayWins = -1;

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

    public void setSeason(Season season) {
        this.season = season;
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

    public boolean hasUser(User user) {
        return home.getMembers().contains(user) || away.getMembers().contains(user);
    }

    public boolean isWinner(Team t) {
        if (t.equals(home) ) {
            return homeRacks > awayRacks;
        }
        return awayRacks > homeRacks;
    }

    public Integer getRacks(Team t) {
        if (t.equals(home) ) {
            return homeRacks;
        }
        return awayRacks;
    }

    public Integer getOpponentRacks(Team t) {
        if (t.equals(home) ) {
            return awayRacks;
        }
        return homeRacks;
    }

    public Integer getSetAwayWins() {
        return setAwayWins;
    }

    public void setSetAwayWins(Integer setAwayWins) {
        this.setAwayWins = setAwayWins;
    }

    public Integer getSetHomeWins() {
        return setHomeWins;
    }

    public void setSetHomeWins(Integer setHomeWins) {
        this.setHomeWins = setHomeWins;
    }

    @Override
    public String toString() {
        return "TeamMatch{" +
                "legacyId=" + getLegacyId() + " " +
                "home=" + (home == null ? "" : home.getName()) +
                ", away=" + (away == null ? "" : away.getName()) +
                ", season=" + season +
                ", matchDate=" + matchDate +
                ", homeRacks=" + homeRacks +
                ", awayRacks=" + awayRacks +
                '}';
    }

    public boolean hasResults() {
        return homeRacks > 0;
    }

    public Integer getSetWins(Team team) {
        if (!team.isNine()) { return isWinner(team) ? 1 : 0; }

        return team.equals(home)? setHomeWins : setAwayWins;
    }

    public Integer getSetLoses(Team team) {
         if (!team.isNine()) { return isWinner(team) ? 1 : 0; }

        return team.equals(home)? setAwayWins : setHomeWins;
    }

    public Integer getWinnerSetWins() {
        if (!season.isNine()) {
            return  homeRacks > awayRacks  ? homeRacks  : awayRacks;
        }

        return homeRacks > awayRacks ? setHomeWins : setAwayWins;
    }

    public Integer getWinnerSetLoses() {
        if (!season.isNine()) {
            return  homeRacks > awayRacks  ? awayRacks  : homeRacks;
        }

        return homeRacks > awayRacks ? setAwayWins : setHomeWins;
    }

    public Integer getLoserSetWins() {
        if (!season.isNine()) {
            return  homeRacks > awayRacks  ? awayRacks  : homeRacks;
        }

        return homeRacks > awayRacks ? setAwayWins : setHomeWins;
    }

    public Integer getLoserSetLoses() {
        if (!season.isNine()) {
            return  homeRacks > awayRacks  ? homeRacks  : awayRacks;
        }

        return homeRacks > awayRacks ? setHomeWins : setAwayWins;
    }
}
