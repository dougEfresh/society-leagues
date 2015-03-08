package com.society.leagues.client.api.domain;

public class UserStats {
    int wins = 0;
    int matches = 0;
    int loses = 0;
    int racks = 0;
    User user;


    public int getWins() {
        return wins;
    }

    public int getMatches() {
        return matches;
    }

    public int getLoses() {
        return loses;
    }

    public int getRacks() {
        return racks;
    }

    public double getPercentage() {
        return new Double(loses)/new Double(racks);
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
