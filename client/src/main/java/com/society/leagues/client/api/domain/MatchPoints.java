package com.society.leagues.client.api.domain;

public class MatchPoints {

    Integer resultId;
    Integer points;
    Double weightedAvg;
    Integer matchNum;
    String calculation;

    public MatchPoints(Integer resultId, Integer points, Double weightedAvg, Integer matchNum, String calculation) {
        this.resultId = resultId;
        this.points = points;
        this.weightedAvg = weightedAvg;
        this.matchNum = matchNum;
        this.calculation = calculation;
    }

    public MatchPoints() {
    }

    public Integer getResultId() {
        return resultId;
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
}
