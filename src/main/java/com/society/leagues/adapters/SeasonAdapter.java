package com.society.leagues.adapters;

import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Status;
import com.society.leagues.client.api.domain.division.Division;
import java.util.List;

public class SeasonAdapter {

    Season season;
    Integer division;
    List<TeamMatchAdapter> teamMatchAdapters;

    public SeasonAdapter(Season season, Division division, List<TeamMatchAdapter> teamMatchAdapters) {
        this.season = season;
        this.division = division == null ? 0 : division.getId();
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
        return division;
    }

    public boolean current() {
        return this.season.getSeasonStatus() == Status.ACTIVE;
    }

    public List<TeamMatchAdapter> getTeamMatches() {
        return teamMatchAdapters;
    }
}
