package com.society.leagues.client.api.domain.league;

public enum LeagueType {
    INDIVIDUAL("Individual"),
    TEAM("Team"),
    MIXED("Mixed");

    public final String name;

    LeagueType(String name) {
        this.name = name;
    }
}
