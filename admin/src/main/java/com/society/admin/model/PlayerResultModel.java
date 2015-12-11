package com.society.admin.model;


import com.society.leagues.client.api.domain.PlayerResult;

import java.util.ArrayList;
import java.util.List;

public class PlayerResultModel {

    List<PlayerResult> playerResults = new ArrayList<>();
    String matchId;

    public PlayerResultModel(List<PlayerResult> playerResults, String matchId) {
        this.playerResults = playerResults;
        this.matchId = matchId;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public PlayerResultModel(List<PlayerResult> playerResults) {
        this.playerResults = playerResults;
    }

    public PlayerResultModel() {
    }

    public List<PlayerResult> getPlayerResults() {
        return playerResults;
    }

    public void setPlayerResults(List<PlayerResult> playerResults) {
        this.playerResults = playerResults;
    }
}
