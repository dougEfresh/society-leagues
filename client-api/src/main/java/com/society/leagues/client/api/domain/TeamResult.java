package com.society.leagues.client.api.domain;

import javax.validation.constraints.NotNull;

public class TeamResult extends LeagueObject {
    @NotNull
    TeamMatch teamMatch;
    Integer homeRacks;
    Integer awayRacks;

    public Integer getHomeRacks() {
        return homeRacks;
    }

    public void setHomeRacks(Integer homeRacks) {
        this.homeRacks = homeRacks;
    }

    public Integer getAwayRacks() {
        return awayRacks;
    }

    public void setAwayRacks(Integer awayRacks) {
        this.awayRacks = awayRacks;
    }

    public TeamMatch getTeamMatch() {
        return teamMatch;
    }

    public void setTeamMatch(TeamMatch teamMatch) {
        this.teamMatch = teamMatch;
    }

    public Integer getWinnerRacks() {
        return homeRacks > awayRacks ? homeRacks : awayRacks;
    }

    public Integer getLoserRacks() {
        return homeRacks > awayRacks ? awayRacks : homeRacks;
    }
}
