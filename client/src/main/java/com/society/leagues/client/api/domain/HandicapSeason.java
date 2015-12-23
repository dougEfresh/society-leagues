package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.omg.CORBA.UNKNOWN;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.NotNull;

public class HandicapSeason {

    @NotNull @DBRef Season season;
    @NotNull Handicap handicap;

    @JsonIgnore
    int index = 0;

    public HandicapSeason() {
    }

    public static HandicapSeason UNKNOWN = new HandicapSeason(Handicap.UNKNOWN,null);

    public HandicapSeason(Handicap handicap, Season season) {
        this.handicap = handicap;
        this.season = season;
    }

    public Handicap getHandicapDisplay() {
        return handicap;
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        HandicapSeason that = (HandicapSeason) o;

        if (!season.equals(that.season)) return false;
        return true;
    }


    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + season.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "HandicapSeason{" +
                "season=" + season +
                ", handicap=" + handicap +
                '}';
    }
}
