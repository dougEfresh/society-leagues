package com.society.leagues.client.api.domain;

public class Stats {
    Integer racksWon = 0;
    Integer racksLost = 0;
    Integer wins = 0;
    Integer loses = 0;
    Integer matches = 0;
    Integer points = 0;

    public Integer getRacksWon() {
        return racksWon;
    }

    public void setRacksWon(Integer racksWon) {
        this.racksWon = racksWon;
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

    public void addWin(int racks, int racksLost) {
        wins++;
        matches++;
        this.racksWon += racks;
        this.racksLost += racksLost;
    }

    public void addLost(int racks, int racksLost) {
        loses++;
        matches++;
        this.racksWon += racks;
        this.racksLost += racksLost;
    }

    public void addWins(int i) {
        wins += i;
    }
    public void addLoses(int i) {
        loses += i;
    }
    public void addMatches(int i) {
        matches += i;
    }
    public void addRacks(int i) {
        racksWon += i;
    }

    public void addRacksLost(int i) {
        racksLost += i;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public Integer getPoints() {
        return points;
    }

    public Integer getRacksLost() {
        return racksLost;
    }

}
