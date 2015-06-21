package com.society.leagues.adapters;

import com.society.leagues.client.api.domain.PlayerResult;

@SuppressWarnings("unused")
public class TeamPlayerResultAdapter {

    PlayerResult result;

    public TeamPlayerResultAdapter(PlayerResult result) {
        this.result = result;
    }

    public TeamPlayerResultAdapter() {

    }

    public Integer getWinnerRacks() {
        if (result.getHomeRacks() > result.getAwayRacks())
            return result.getHomeRacks();

        return result.getAwayRacks();
    }

    public Integer getLoserRacks() {
        if (result.getHomeRacks() > result.getAwayRacks())
            return result.getAwayRacks();

        return result.getHomeRacks();
    }

    public Integer getWinner() {
        if (result.getHomeRacks() > result.getAwayRacks())
            return result.getPlayerHome().getUserId();

        return result.getPlayerAway().getUserId();
    }

    public Integer getLoser() {
        if (result.getHomeRacks() > result.getAwayRacks())
            return result.getPlayerAway().getUserId();

        return result.getPlayerHome().getUserId();
    }
}
