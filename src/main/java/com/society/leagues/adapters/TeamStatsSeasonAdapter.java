package com.society.leagues.adapters;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

public class TeamStatsSeasonAdapter {

    Map<String,Object> stats;

    public TeamStatsSeasonAdapter(Map<String,Object> stats) {
        this.stats = stats;
    }

    public TeamStatsSeasonAdapter() {

    }

    public Integer getTeamId() {
        return (Integer) stats.get("team_id");
    }

    public Integer seasonId() {
        return (Integer) stats.get("season_id");
    }

    public Integer getWins() {
        return ((BigInteger) stats.get("wins")).intValue();
    }

    public Integer getLost() {
        return ((BigInteger) stats.get("loses")).intValue();
    }

    public Integer getRacksFor() {
        return ((BigInteger) stats.get("racks_for")).intValue();
    }

    public Integer getRacksAgainsts() {

        return ((BigInteger) stats.get("racks_against")).intValue();
    }

    public Integer getSetWins() {
        return ((BigInteger) stats.get("setWins")).intValue();
    }

    public Integer getSetLoses() {
        return ((BigInteger) stats.get("setLoses")).intValue();
    }
}
