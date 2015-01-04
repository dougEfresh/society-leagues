package com.society.leagues.client.api.domain.league;

public class League {
    LeagueType type;
    Integer id;

    public League(LeagueType type) {
        this.type = type;
    }

    public League() {
    }

    public LeagueType getType() {
        return type;
    }

    public void setType(LeagueType type) {
        this.type = type;
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
                ", id=" + id +
                '}';
    }
}
