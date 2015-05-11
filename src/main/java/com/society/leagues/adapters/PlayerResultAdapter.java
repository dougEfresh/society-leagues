package com.society.leagues.adapters;


import com.society.leagues.client.api.domain.*;

public class PlayerResultAdapter {
    PlayerResult result;
    User user;

    public PlayerResultAdapter() {
    }

    public PlayerResultAdapter(User user, PlayerResult result) {
        this.user = user;
        this.result = result;
    }

    public Player winner() {
        if (result.getHomeRacks() > result.getAwayRacks()) {
            return result.getPlayerHome();
        }

        return result.getPlayerAway();
    }

    public boolean isWin() {
        Player winner = winner();
        return winner.getUser().equals(user.getId());
    }

    public Integer getRacksFor() {
        if (this.result.getPlayerHome().getUserId().equals(user.getId())) {
            return this.result.getHomeRacks();
        }
        return this.result.getAwayRacks();
    }

    public Integer getRacksAgainst() {
        if (this.result.getPlayerHome().getUserId().equals(user.getId())) {
            return this.result.getAwayRacks();
        }

        return this.result.getHomeRacks();
    }

    public Handicap getHandicap() {
        if (result.getPlayerHome().getUser().equals(user)) {
            return result.getPlayerHome().getHandicap();
        }

        return result.getPlayerAway().getHandicap();
    }

    public Handicap getOpponentHandicap() {
        if (result.getPlayerHome().getUser().equals(user)) {
            return result.getPlayerAway().getHandicap();
        }

        return result.getPlayerHome().getHandicap();
    }

    public Integer getOpponent() {
        if (this.result.getPlayerHome().getUserId().equals(user.getId())) {
            return this.result.getPlayerAway().getUserId();
        }

        return this.result.getPlayerHome().getUserId();
    }

    public Integer getOpponentTeam() {
        if (this.result.getPlayerHome().getUserId().equals(user.getId())) {
            return this.result.getPlayerAway().getTeam().getId();
        }

        return this.result.getPlayerHome().getTeam().getId();
    }

    public TeamMatch teamMatch() {
        return this.result.getTeamMatch();
    }

    public Integer getTeamMatchId() {
        return result.getTeamMatch().getId();
    }
}
