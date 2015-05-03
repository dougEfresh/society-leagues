package com.society.leagues.adapters;

import com.society.leagues.client.api.domain.PlayerResult;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.client.api.domain.TeamResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TeamMatchAdapter {

    TeamMatch teamMatch;
    TeamResult teamResult;
    List<PlayerResult> playerResults;

    public TeamMatchAdapter(TeamMatch teamMatch, TeamResult result, List<PlayerResult> playerResults) {
        this.teamResult = result;
        this.teamMatch = teamMatch;
        this.playerResults = playerResults;
    }

    public TeamMatchAdapter() {
    }

    public Integer getTeamMatchId() {
        return teamMatch.getId();
    }

    public Integer getHome() {
        return teamMatch.getHome().getId();
    }

    public Integer getAway() {
        return teamMatch.getAway().getId();
    }

    public LocalDateTime getMatchDate() {
        return teamMatch.getMatchDate();
    }

    public Integer getResultId() {
        if (teamResult == null) {
            return 0;
        }
        return teamResult.getId();
    }
    public Integer getHomeRacks() {
        if (teamResult == null) {
            return 0;
        }
        return teamResult.getHomeRacks();
    }
    public Integer getAwayRacks() {
        if (teamResult == null) {
            return 0;
        }
        return teamResult.getAwayRacks();
    }

    public List<TeamPlayerResultAdapter> getTeamResults() {
        return playerResults.stream().map(TeamPlayerResultAdapter::new).collect(Collectors.toList());
    }

}
