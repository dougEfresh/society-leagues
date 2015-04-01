package com.society.leagues.client.api.domain;

public class PlayerStats {
    String name;
    Integer userId;
    Player player;
    Integer racks = 0;
    Integer wins = 0;
    Integer loses = 0;
    Integer matches = 0;
    Integer points = 0;

    public PlayerStats() {
    }

    public PlayerStats(Player player) {
        this.player = player;
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

    public double getPercentage() {
        return new Double(loses)/new Double(matches);
    }

    public void addWin(int racks) {
        wins++;
        matches++;
        this.racks += racks;
    }

    public void addLost(int racks) {
        loses++;
        matches++;
        this.racks += racks;
    }

    public void addPoints(int points) {
        this.points += points;
    }

}
