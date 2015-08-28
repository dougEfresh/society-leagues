package com.society.leagues.client.api.domain;

import org.springframework.data.mongodb.core.mapping.DBRef;

public class HandicapSeason extends LeagueObject {

    @DBRef Season season;
    Handicap handicap;

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
}
