package com.society.leagues.adapters;

import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class SeasonAdapter {

    Season season;
    Map<LocalDateTime,List<TeamMatchAdapter>> teamMatchAdapters;

    public SeasonAdapter(Season season, Map<LocalDateTime,List<TeamMatchAdapter>> teamMatchAdapters) {
        this.season = season;
        this.teamMatchAdapters = teamMatchAdapters;
    }

    public SeasonAdapter() {
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public Integer getDivision() {
        return season.getDivision().getId();
    }

    public boolean current() {
        return this.season.getSeasonStatus() == Status.ACTIVE;
    }

    public Map<LocalDateTime,List<TeamMatchAdapter>> getTeamMatches() {
        return teamMatchAdapters;
    }
}
