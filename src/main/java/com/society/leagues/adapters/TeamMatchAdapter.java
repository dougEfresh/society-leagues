package com.society.leagues.adapters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.PlayerResult;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.client.api.domain.TeamResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class TeamMatchAdapter {

    TeamMatch teamMatch;
    TeamResult teamResult;
    List<PlayerResult> playerResults;
    List<TeamPlayerResultAdapter> results;

    public TeamMatchAdapter(TeamMatch teamMatch, TeamResult result, List<PlayerResult> playerResults) {
        this.teamResult = result;
        this.teamMatch = teamMatch;
        this.playerResults = playerResults;
        this.results = playerResults.stream().map(TeamPlayerResultAdapter::new).collect(Collectors.toList());
    }

    public TeamMatchAdapter() {
    }

    public Integer getTeamMatchId() {
        return teamMatch.getId();
    }

    @JsonIgnore
    public Integer getResultId() {
        if (teamResult == null) {
            return 0;
        }
        return teamResult.getId();
    }

    public Integer getWinner() {
        if (teamResult == null) {
            return 0;
        }
        if (teamResult.getHomeRacks() > teamResult.getAwayRacks() ) {
            return teamMatch.getHome().getId();
        }
        return teamMatch.getAway().getId();
    }

    public Integer getLoser() {
         if (teamResult == null) {
            return 0;
        }
        if (teamResult.getHomeRacks() > teamResult.getAwayRacks() ) {
            return teamMatch.getAway().getId();
        }
        return teamMatch.getHome().getId();
    }

    public Integer getWinnerRacks() {
        if (teamResult == null) {
            return 0;
        }
        if (teamResult.getHomeRacks() > teamResult.getAwayRacks()) {
            return teamResult.getHomeRacks();
        }

        return teamResult.getAwayRacks();
    }

    public Integer getLoserRacks() {
        if (teamResult == null) {
            return 0;
        }
        if (teamResult.getHomeRacks() > teamResult.getAwayRacks()) {
            return teamResult.getAwayRacks();
        }

        return teamResult.getHomeRacks();
    }

    public LocalDateTime matchDate() {
        return teamMatch.getMatchDate();
    }

    public List<TeamPlayerResultAdapter> getTeamResults() {
        return this.results;
    }

    public Integer getWinnerSetWins() {
        int wins = 0;
        if (playerResults == null || playerResults.isEmpty()) {
            return wins;
        }
        List<Player> winners = getTeamResults().stream().map(tr -> find(tr.getWinner())).collect(Collectors.toList());

        for (Player winner : winners) {
            if (winner.getTeam().getId().equals(getWinner())) {
                wins++;
            }
        }
        return wins;
    }

     public Integer getWinnerSetLoses() {
        int loses = 0;
        if (playerResults == null || playerResults.isEmpty()) {
            return loses;
        }
        List<Player> losers = getTeamResults().stream().map(tr -> find(tr.getLoser())).collect(Collectors.toList());

        for (Player loser : losers) {
            if (loser.getTeam().getId().equals(getWinner())) {
                loses++;
            }
        }
        return loses;
    }
   public Integer getLoserSetWins() {
        int wins = 0;
        if (playerResults == null || playerResults.isEmpty()) {
            return wins;
        }
        List<Player> winners = getTeamResults().stream().map(tr -> find(tr.getWinner())).collect(Collectors.toList());
        for (Player winner : winners) {
            if (winner.getTeam().getId().equals(getLoser())) {
                wins++;
            }
        }
        return wins;
    }

     public Integer getLoserSetLoses() {
        int loses = 0;
        if (playerResults == null || playerResults.isEmpty()) {
            return loses;
        }
        List<Player> losers = getTeamResults().stream().map(tr -> find(tr.getLoser())).collect(Collectors.toList());
        for (Player loser : losers) {
            if (loser.getTeam().getId().equals(getLoser())) {
                loses++;
            }
        }
        return loses;
    }

    private Player find(Integer u) {
        for (PlayerResult playerResult : playerResults) {
            if (playerResult.getPlayerAway().getUserId().equals(u)) {
                return playerResult.getPlayerAway();
            }

            if (playerResult.getPlayerHome().getUserId().equals(u)) {
                return playerResult.getPlayerHome();
            }
        }
        //Should never get here
        return null;
    }

}
