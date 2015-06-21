package com.society.leagues.adapters;

import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Team;

public class TeamSeasonAdapter {

    Team team;
    Season season;

    public TeamSeasonAdapter() {
    }

    public TeamSeasonAdapter(Team team, Season season) {
        this.team = team;
        this.season = season;
    }

    public Integer getId() {
        return team.getId();
    }

    public Integer getSeason() {
        return season.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeamSeasonAdapter)) return false;

        TeamSeasonAdapter that = (TeamSeasonAdapter) o;

        if (!season.equals(that.season)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return season.hashCode();
    }
}
