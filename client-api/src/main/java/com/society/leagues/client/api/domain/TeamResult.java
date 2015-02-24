package com.society.leagues.client.api.domain;

import javax.validation.constraints.NotNull;

public class TeamResult extends LeagueObject {
    @NotNull
    Match match;
    Integer homeRacks;
    Integer awayRacks;

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Integer getHomeRacks() {
        return homeRacks;
    }

    public void setHomeRacks(Integer homeRacks) {
        this.homeRacks = homeRacks;
    }

    public Integer getAwayRacks() {
        return awayRacks;
    }

    public void setAwayRacks(Integer awayRacks) {
        this.awayRacks = awayRacks;
    }

    public Team getWinner() {
        if (homeRacks > awayRacks) {
            return match.getHome();
        }

        return match.getAway();
    }

    public Team getLoser() {
        if (homeRacks < awayRacks) {
            return match.getHome();
        }

        return match.getAway();
    }
}
