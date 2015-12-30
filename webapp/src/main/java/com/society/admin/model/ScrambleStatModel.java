package com.society.admin.model;


import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Stat;
import com.society.leagues.client.api.domain.User;

public class ScrambleStatModel {

    Stat mixedEight;
    Stat mixedNine;
    Stat mixedScotch;

    public ScrambleStatModel() {
    }

    public ScrambleStatModel(Stat mixedEight, Stat mixedNine, Stat mixedScotch) {
        this.mixedEight = mixedEight;
        this.mixedNine = mixedNine;
        this.mixedScotch = mixedScotch;
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

    public Stat getMixedScotch() {
        return mixedScotch;
    }

    public void setMixedScotch(Stat mixedScotch) {
        this.mixedScotch = mixedScotch;
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

    public int getRank() {
        return 0;
    }

    public String getHandicapDisplay() {
        return mixedEight.getHandicapDisplay();
    }

    public User getUser() {
        return mixedEight.getUser();
    }

    public Season getSeason() {
        return mixedEight.getSeason();
    }
}
