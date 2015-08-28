package com.society.leagues.client.api.domain;


import org.springframework.data.mongodb.core.mapping.DBRef;

public class PlayerResult extends LeagueObject {

    @DBRef TeamMatch teamMatch;
    @DBRef User playerHome;
    @DBRef User playerAway;
    Integer homeRacks;
    Integer awayRacks;
    Integer matchNumber;

    public TeamMatch getTeamMatch() {
        return teamMatch;
    }

    public void setTeamMatch(TeamMatch teamMatch) {
        this.teamMatch = teamMatch;
    }

    public User getPlayerHome() {
        return playerHome;
    }

    public void setPlayerHome(User playerHome) {
        this.playerHome = playerHome;
    }

    public User getPlayerAway() {
        return playerAway;
    }

    public void setPlayerAway(User playerAway) {
        this.playerAway = playerAway;
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

    public Integer getMatchNumber() {
        return matchNumber;
    }

    public void setMatchNumber(Integer matchNumber) {
        this.matchNumber = matchNumber;
    }

    @Override
    public String toString() {
        return "PlayerResult{" +
                "teamMatch=" + teamMatch +
                ", playerHome=" + playerHome.getId() +
                ", playerAway=" + playerAway.getId() +
                ", homeRacks=" + homeRacks +
                ", awayRacks=" + awayRacks +
                '}';
    }
}
