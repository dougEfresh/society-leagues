package com.society.leagues.client.api.domain;


import com.society.leagues.client.api.domain.division.Division;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TeamMatch extends LeagueObject  {
    @NotNull Team home;
    @NotNull Team away;
    @NotNull Season season;
    @NotNull Division division;
    Date matchDate;
    TeamResult result;
    Boolean win;
    List<PlayerResult> playerResults = Collections.emptyList();

    public TeamMatch(Team home, Team away, Season season, Date matchDate) {
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

    public TeamResult getResult() {
        return result;
    }

    public void setResult(TeamResult result) {
        this.result = result;
    }

    public Team getWinner() {
        if (result == null)
            return null;

        if (result.getHomeRacks() > result.getAwayRacks()) {
            return home;
        }

        return away;
    }

    public Team getLoser() {
        if (result == null)
            return null;

        if (result.getHomeRacks() < result.getAwayRacks()) {
            return home;
        }

        return away;
    }

    public Integer getWinnerRacks() {
        if (getWinner() == null)
            return null;

        return result.getWinnerRacks();
    }

    public Integer getLoserRacks() {
        if (getLoser() == null)
            return null;

        return result.getLoserRacks();
    }

    public Boolean isWin() {
        return win;
    }

    public void setWin(Boolean win) {
        this.win = win;
    }

    public List<PlayerResult> getPlayerResults() {
        return playerResults;
    }

    public void setPlayerResults(List<PlayerResult> playerResults) {
        this.playerResults = playerResults;
    }
}
