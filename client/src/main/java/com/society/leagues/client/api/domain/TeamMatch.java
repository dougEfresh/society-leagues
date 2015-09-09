package com.society.leagues.client.api.domain;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.society.leagues.client.api.domain.converters.DateTimeDeSerializer;
import com.society.leagues.client.api.domain.converters.DateTimeSerializer;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class TeamMatch extends LeagueObject {

    @DBRef Team home;
    @DBRef Team away;
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeSerializer.class)
    @NotNull LocalDateTime matchDate;

    Integer homeRacks = -1;
    Integer awayRacks = -1;
    Integer setHomeWins = 0;
    Integer setAwayWins = 0;

    public TeamMatch(Team home, Team away, LocalDateTime matchDate) {
        this.home = home;
        this.away = away;
        this.matchDate = matchDate;
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
        return home.getSeason();
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

    public boolean hasTeam(Team team) {
        return home.equals(team) || away.equals(team);
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
        if (!getSeason().isNine()) {
            return  homeRacks > awayRacks  ? homeRacks  : awayRacks;
        }

        return homeRacks > awayRacks ? setHomeWins : setAwayWins;
    }

    public Integer getWinnerSetLoses() {
        if (!getSeason().isNine()) {
            return  homeRacks > awayRacks  ? awayRacks  : homeRacks;
        }

        return homeRacks > awayRacks ? setAwayWins : setHomeWins;
    }

    public Integer getLoserSetWins() {
        if (!getSeason().isNine()) {
            return  homeRacks > awayRacks  ? awayRacks  : homeRacks;
        }

        return homeRacks > awayRacks ? setAwayWins : setHomeWins;
    }

    public Integer getLoserSetLoses() {
        if (!getSeason().isNine()) {
            return  homeRacks > awayRacks  ? homeRacks  : awayRacks;
        }

        return homeRacks > awayRacks ? setHomeWins : setAwayWins;
    }

    @Override
    public String toString() {
        return "TeamMatch{" +
                "home=" + home +
                ", away=" + away +
                ", matchDate=" + matchDate +
                ", homeRacks=" + homeRacks +
                ", awayRacks=" + awayRacks +
                ", setHomeWins=" + setHomeWins +
                ", setAwayWins=" + setAwayWins +
                '}';
    }
}
