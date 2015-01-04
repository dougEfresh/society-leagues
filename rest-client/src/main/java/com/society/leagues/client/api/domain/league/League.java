package com.society.leagues.client.api.domain.league;

public class League {
    LeagueType type;
    Double dues;
    Integer id;

    public League(LeagueType type, Double dues, Integer id) {
        this.type = type;
        this.dues = dues;
        this.id = id;
    }

    public League(LeagueType type, Double dues) {
        this.type = type;
        this.dues = dues;
    }

    public League() {
    }

    public LeagueType getType() {
        return type;
    }

    public void setType(LeagueType type) {
        this.type = type;
    }

    public Double getDues() {
        return dues;
    }

    public void setDues(Double fee) {
        this.dues = fee;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }



    @Override
    public String toString() {
        return "League{" +
                "type=" + type +
                ", dues=" + dues +
                ", id=" + id +
                '}';
    }
}
