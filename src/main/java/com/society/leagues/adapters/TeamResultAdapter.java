package com.society.leagues.adapters;

import com.society.leagues.client.api.domain.TeamResult;

public class TeamResultAdapter {

    TeamResult teamResult;
    TeamMatchAdapter teamMatchAdapter;

    public TeamResultAdapter(TeamResult teamResult, TeamMatchAdapter teamMatchAdapter) {
        this.teamResult = teamResult;
        this.teamMatchAdapter = teamMatchAdapter;
    }

    public TeamResultAdapter() {
    }

    public Integer getHomeRacks() {
        return teamResult.getHomeRacks();
    }

    public Integer getAwayRacks() {
        return teamResult.getAwayRacks();
    }

    public Integer getWinner() {
        if (teamResult.getHomeRacks() > teamResult.getAwayRacks()) {
            return teamMatchAdapter.getHome();
        }
        return teamMatchAdapter.getAway();
    }

     public Integer getLoser() {
        if (teamResult.getHomeRacks() > teamResult.getAwayRacks()) {
            return teamMatchAdapter.getAway();
        }
        return teamMatchAdapter.getHome();
    }

    public Integer getId() {
        return teamResult.getId();
    }

    public TeamMatchAdapter getTeamMatch() {
        return teamMatchAdapter;
    }
}
