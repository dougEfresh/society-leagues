package com.society.leagues.adapters;

import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.PlayerResult;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.client.api.domain.TeamResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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

    public Integer getResultId() {
        if (teamResult == null) {
            return  null;
        }
        return teamResult.getId();
    }

    public Integer getWinner() {
        if (teamResult == null) {
            return teamMatch.getHome().getId();
        }
        if (teamResult.getHomeRacks() > teamResult.getAwayRacks() ) {
            return teamMatch.getHome().getId();
        }
        return teamMatch.getAway().getId();
    }

    public Integer getLoser() {
        if (teamResult == null) {
            return teamMatch.getAway().getId();
        }
        if (teamResult.getHomeRacks() > teamResult.getAwayRacks() ) {
            return teamMatch.getAway().getId();
        }
        return teamMatch.getHome().getId();
    }

    public Integer getWinnerRacks() {
        if (teamResult == null) {
            return null;
        }
        if (teamResult.getHomeRacks() > teamResult.getAwayRacks()) {
            return teamResult.getHomeRacks();
        }

        return teamResult.getAwayRacks();
    }

    public Integer getLoserRacks() {
        if (teamResult == null) {
            return null;
        }
        if (teamResult.getHomeRacks() > teamResult.getAwayRacks()) {
            return teamResult.getAwayRacks();
        }

        return teamResult.getHomeRacks();
    }

    public LocalDate getMatchDate() {
        return teamMatch.getMatchDate().toLocalDate();
    }

    public List<TeamPlayerResultAdapter> teamResults() {
        return this.results;
    }

    public Integer getWinnerSetWins() {
        int wins = 0;
        if (playerResults == null || playerResults.isEmpty()) {
            return null;
        }
        List<Player> winners = teamResults().stream().map(tr -> find(tr.getWinner())).collect(Collectors.toList());

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
            return null;
        }
        List<Player> losers = teamResults().stream().map(tr -> find(tr.getLoser())).collect(Collectors.toList());

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
            return null;
        }
        List<Player> winners = teamResults().stream().map(tr -> find(tr.getWinner())).collect(Collectors.toList());
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
            return null;
        }
        List<Player> losers = teamResults().stream().map(tr -> find(tr.getLoser())).collect(Collectors.toList());
        for (Player loser : losers) {
            if (loser.getTeam().getId().equals(getLoser())) {
                loses++;
            }
        }
        return loses;
    }

    public Integer getSeasonId() {
        return teamMatch.getSeason().getId();
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
