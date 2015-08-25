package com.society.leagues.client.api.domain;

public class MatchPoints {

    Integer matchId;

    Integer points;

    Double weightedAvg;

    Integer matchNum;

    public MatchPoints(Integer matchId, Integer points, Double weightedAvg, Integer matchNum) {
        this.matchId = matchId;
        this.points = points;
        this.weightedAvg = weightedAvg;
        this.matchNum = matchNum;
    }

    public MatchPoints() {
    }

    public Integer getMatchId() {
        return matchId;
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
}
