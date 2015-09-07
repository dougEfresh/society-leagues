package com.society.leagues.client.api.domain;

import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.NotNull;

public class HandicapSeason extends LeagueObject {

    @NotNull @DBRef Season season;
    @NotNull Handicap handicap;

    public HandicapSeason() {
    }

    public HandicapSeason(Handicap handicap, Season season) {
        this.handicap = handicap;
        this.season = season;
    }

    public Handicap getHandicap() {
        return handicap;
    }

    public void setHandicap(Handicap handicap) {
        this.handicap = handicap;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        HandicapSeason that = (HandicapSeason) o;

        if (!season.equals(that.season)) return false;
        return handicap == that.handicap;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + season.hashCode();
        result = 31 * result + handicap.hashCode();
        return result;
    }
}
