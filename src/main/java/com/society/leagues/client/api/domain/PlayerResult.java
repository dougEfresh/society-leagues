package com.society.leagues.client.api.domain;


public class PlayerResult extends LeagueObject {
    TeamMatch teamMatch;
    Player playerHome;
    Player playerAway;
    Integer homeRacks;
    Integer awayRacks;

    public TeamMatch getTeamMatch() {
        return teamMatch;
    }

    public void setTeamMatch(TeamMatch teamMatch) {
        this.teamMatch = teamMatch;
    }

    public Player getPlayerHome() {
        return playerHome;
    }

    public void setPlayerHome(Player playerHome) {
        this.playerHome = playerHome;
    }

    public Player getPlayerAway() {
        return playerAway;
    }

    public void setPlayerAway(Player playerAway) {
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
