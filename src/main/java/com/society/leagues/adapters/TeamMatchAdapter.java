package com.society.leagues.adapters;

import com.society.leagues.client.api.domain.TeamMatch;

import java.time.LocalDateTime;

public class TeamMatchAdapter {

    TeamMatch teamMatch;

    public TeamMatchAdapter(TeamMatch teamMatch) {
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
}
