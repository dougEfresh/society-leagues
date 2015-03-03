package com.society.leagues.client.api.domain;

public class PlayerResult extends LeagueObject {
    Integer teamMatchId;
    Integer playerHomeId;
    Integer playerAwayId;
    Integer homeRacks;
    Integer awayRacks;

    public Integer getHomeRacks() {
        return homeRacks;
    }

    public Integer getTeamMatchId() {
        return teamMatchId;
    }

    public void setTeamMatchId(Integer teamMatchId) {
        this.teamMatchId = teamMatchId;
    }

    public Integer getPlayerHomeId() {
        return playerHomeId;
    }

    public void setPlayerHomeId(Integer playerHomeId) {
        this.playerHomeId = playerHomeId;
    }

    public Integer getPlayerAwayId() {
        return playerAwayId;
    }

    public void setPlayerAwayId(Integer playerAwayId) {
        this.playerAwayId = playerAwayId;
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
}
