package com.society.leagues.adapters;

import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.client.api.domain.TeamResult;

import java.time.LocalDateTime;

public class TeamMatchAdapter {

    TeamMatch teamMatch;
    TeamResult teamResult;

    public TeamMatchAdapter(TeamMatch teamMatch, TeamResult result) {
        this.teamResult = result;
        this.teamMatch = teamMatch;
    }

    public TeamMatchAdapter() {
    }

    public Integer getId() {
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
}
