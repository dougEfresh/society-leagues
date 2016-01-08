package com.society.admin.model;


import com.society.leagues.client.api.domain.PlayerResult;
import com.society.leagues.client.api.domain.TeamMatch;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerResultModel {

    List<PlayerResult> playerResults = new ArrayList<>();
    String matchId;

    public PlayerResultModel(List<PlayerResult> playerResults, String matchId) {
        this.playerResults = playerResults;
        this.matchId = matchId;
    }

    public String getId() {
        return matchId;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public PlayerResultModel() {
    }

    public List<PlayerResult> getPlayerResults() {
        return playerResults;
    }

    public void setPlayerResults(List<PlayerResult> playerResults) {
        this.playerResults = playerResults;
    }

    public String getName() {
        if (playerResults.isEmpty())
            return "";

        TeamMatch tm = playerResults.iterator().next().getTeamMatch();

        return String.format("%s vs %s", tm.getHome().getName(), tm.getAway().getName());
    }

    public String getRowClass(Integer index) {
        return playerResults.get(index).getSetNumber() % 2 == 0 ? "even" : "odd";
    }

    public List<PlayerResult> getNoForfeits() {
        return playerResults.stream().filter(r->!r.isForfeit()).collect(Collectors.toList());
    }
}
