package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotNull;

public class MatchPoints {
    PlayerResult playerResult;
    Integer points = 0;
    Double weightedAvg = 0d;
    Integer matchNum = 0;
    String calculation = "";
    User user;

    public MatchPoints() {
    }

    public Integer getPoints() {
        return points;
    }

    public Double getWeightedAvg() {
        return weightedAvg;
    }

    public Integer getMatchNum() {
        return matchNum;
    }

    public String getCalculation() {
        return calculation;
    }

    @JsonIgnore
    public PlayerResult getPlayerResult() {
        return playerResult;
    }

    public void setPlayerResult(PlayerResult playerResult) {
        this.playerResult = playerResult;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public void setWeightedAvg(Double weightedAvg) {
        this.weightedAvg = weightedAvg;
    }

    public void setMatchNum(Integer matchNum) {
        this.matchNum = matchNum;
    }

    public void setCalculation(String calculation) {
        this.calculation = calculation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
