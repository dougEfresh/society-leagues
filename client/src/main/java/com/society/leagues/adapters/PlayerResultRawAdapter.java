package com.society.leagues.adapters;

import com.society.leagues.client.api.domain.Handicap;
import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.PlayerResult;

import java.time.LocalDate;

@SuppressWarnings("unused")
public class PlayerResultRawAdapter {

    PlayerResult result;

    public PlayerResultRawAdapter() {
    }

    public PlayerResultRawAdapter(PlayerResult result) {
        this.result = result;
    }

    public Player winner() {
        if (result.getHomeRacks() > result.getAwayRacks()) {
            return result.getPlayerHome();
        }

        return result.getPlayerAway();
    }

    public Integer getWinner() {
        if (result.getHomeRacks() > result.getAwayRacks()) {
            return result.getPlayerHome().getUserId();
        }

        return result.getPlayerAway().getUserId();
    }

    public Integer getLoser() {
        if (result.getHomeRacks() > result.getAwayRacks()) {
            return result.getPlayerAway().getUserId();
        }

        return result.getPlayerHome().getUserId();
    }

    public Integer getWinnerRacks() {
        if (result.getHomeRacks() > result.getAwayRacks()) {
            return result.getHomeRacks();
        }

        return result.getAwayRacks();
    }

    public Integer getLoserRacks() {
        if (result.getHomeRacks() > result.getAwayRacks()) {
            return result.getAwayRacks();
        }

        return result.getHomeRacks();
    }

    public Handicap getWinnerHandicap() {
        if (result.getHomeRacks() > result.getAwayRacks()) {
            return result.getPlayerHome().getHandicap();
        }
        return result.getPlayerAway().getHandicap();
    }

    public Handicap getLoserHandicap() {
        if (result.getHomeRacks() > result.getAwayRacks()) {
            return result.getPlayerAway().getHandicap();
        }
        return result.getPlayerHome().getHandicap();
    }

    public Integer getWinnerTeam() {
        if (result.getHomeRacks() > result.getAwayRacks()) {
            return result.getPlayerHome().getTeam().getId();
        }
        return result.getPlayerAway().getTeam().getId();
    }

    public Integer getLoserTeam() {
        if (result.getHomeRacks() > result.getAwayRacks()) {
            return result.getPlayerAway().getTeam().getId();
        }
        return result.getPlayerHome().getTeam().getId();
    }

    public LocalDate getMatchDate() {
        return result.getTeamMatch().getMatchDate().toLocalDate();
    }

    public Integer getTeamMatchId() {
        return result.getTeamMatch().getId();
    }

    public Integer getSeasonId() {
        return result.getTeamMatch().getSeason().getId();
    }

}
