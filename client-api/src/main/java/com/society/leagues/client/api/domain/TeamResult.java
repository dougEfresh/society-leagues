package com.society.leagues.client.api.domain;

import javax.validation.constraints.NotNull;

public class TeamResult extends LeagueObject {
    @NotNull
    Integer teamMatchId;
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

    public Integer getTeamMatchId() {
        return teamMatchId;
    }

    public void setTeamMatchId(Integer teamMatchId) {
        this.teamMatchId = teamMatchId;
    }
}
