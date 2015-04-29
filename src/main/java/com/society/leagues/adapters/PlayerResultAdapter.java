package com.society.leagues.adapters;


import com.society.leagues.client.api.domain.PlayerResult;

public class PlayerResultAdapter {
    PlayerResult result;

    public PlayerResultAdapter() {
    }

    public PlayerResultAdapter(PlayerResult result) {
        this.result = result;
    }

    public Integer getHome() {
        return result.getPlayerHome().getId();
    }

    public Integer getAway() {
        return result.getPlayerAway().getId();
    }

    public Integer getHomeRacks() {
        return result.getHomeRacks();
    }

    public Integer getAwayRacks() {
        return result.getAwayRacks();
    }

    public Integer getTeamMatchId() {
        return result.getTeamMatch().getId();
    }

    public Integer getId() {
        return result.getId();
    }
}
