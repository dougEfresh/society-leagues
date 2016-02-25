package com.society.leagues.model;


import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Stat;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.User;

public class ScrambleStatModel {

    Stat mixedEight;
    Stat mixedNine;
    Stat seasonStats;

    public ScrambleStatModel() {
    }

    public ScrambleStatModel(Stat mixedEight, Stat mixedNine, Stat seasonStats) {
        this.mixedEight = mixedEight;
        this.mixedNine = mixedNine;
        this.seasonStats = seasonStats;
    }

    public Stat getMixedEight() {
        return mixedEight;
    }

    public void setMixedEight(Stat mixedEight) {
        this.mixedEight = mixedEight;
    }

    public Stat getMixedNine() {
        return mixedNine;
    }

    public void setMixedNine(Stat mixedNine) {
        this.mixedNine = mixedNine;
    }

    public Stat getSeasonStats() {
        return seasonStats;
    }

    public void setSeasonStats(Stat seasonStats) {
        this.seasonStats = seasonStats;
    }

    public int getEightWins() {
        return mixedEight.getWins();
    }
    public double getEightWinsPct() {
        return mixedEight.getWinPct();
    }
    public int getEightLoses() {
        return mixedEight.getLoses();
    }

    public int getNineWins() {
        return mixedNine.getWins();
    }
    public double getNineWinsPct() {
        return mixedNine.getWinPct();
    }
    public int getNineLoses() {
        return mixedNine.getLoses();
    }

    public User getUser() {
        return  seasonStats.getUser();
    }

    public Team getTeam() {
        return  seasonStats.getTeam();
    }

    public int getRank() {
        return seasonStats.getRank();
    }

    public String getHandicapDisplay() {
        return seasonStats.getHandicapDisplay();
    }

    public Season getSeason() {
        return seasonStats.getSeason();
    }
}
