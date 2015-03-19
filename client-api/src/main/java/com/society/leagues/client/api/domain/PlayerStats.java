package com.society.leagues.client.api.domain;

public class PlayerStats {
    String name;
    Integer userId;
    Integer playerId;
    Integer racks = 0;
    Integer wins = 0;
    Integer loses = 0;
    Integer matches = 0;

    public PlayerStats() {
    }

    public PlayerStats(Integer playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRacks() {
        return racks;
    }

    public void setRacks(Integer racks) {
        this.racks = racks;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getLoses() {
        return loses;
    }

    public void setLoses(Integer loses) {
        this.loses = loses;
    }

    public Integer getMatches() {
        return matches;
    }

    public void setMatches(Integer matches) {
        this.matches = matches;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }
}
