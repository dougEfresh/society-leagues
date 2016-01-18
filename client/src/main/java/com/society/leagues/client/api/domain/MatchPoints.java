package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.society.leagues.client.views.PlayerResultSummary;

import javax.validation.constraints.NotNull;

public class MatchPoints {
    PlayerResult playerResult;
    @JsonView(PlayerResultSummary.class) Integer points = 0;
    @JsonView(PlayerResultSummary.class) Double weightedAvg = 0d;
    @JsonView(PlayerResultSummary.class) Integer matchNum = 0;
    @JsonView(PlayerResultSummary.class) String calculation = "";
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
