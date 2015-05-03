package com.society.leagues.adapters;

import com.society.leagues.client.api.domain.PlayerResult;

import java.util.ArrayList;
import java.util.List;

public class TeamPlayerResultAdapter {

    PlayerResult result;

    public TeamPlayerResultAdapter(PlayerResult result) {
        this.result = result;
    }

    public TeamPlayerResultAdapter() {

    }

    public Integer getHomeRacks() {
        return result.getHomeRacks();
    }

    public Integer getAwayRacks() {
        return result.getAwayRacks();
    }

    public Integer getHomeUser() {
        return result.getPlayerHome().getUserId();
    }

    public Integer getAwayUser() {
        return result.getPlayerAway().getUserId();
    }
}
