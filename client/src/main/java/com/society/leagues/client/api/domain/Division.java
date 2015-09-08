package com.society.leagues.client.api.domain;

public enum Division {
    
    EIGHT_BALL_WEDNESDAYS(LeagueType.TEAM),
    EIGHT_BALL_THURSDAYS(LeagueType.TEAM),
    MIXED_MONDAYS(LeagueType.MIXED),
    NINE_BALL_TUESDAYS(LeagueType.TEAM),
    EIGHT_BALL_CHALLENGE(LeagueType.INDIVIDUAL),
    STRAIGHT(LeagueType.INDIVIDUAL),
    NINE_BALL_CHALLENGE(LeagueType.INDIVIDUAL);

    final LeagueType leagueType;

    Division(LeagueType leagueType) {
        this.leagueType = leagueType;
    }

    public LeagueType getLeagueType() {
        return leagueType;
    }

    public static boolean isChallange(Division type) {
        return type == EIGHT_BALL_CHALLENGE || type == NINE_BALL_CHALLENGE;
    }

    public boolean isChallenge() {
        return this == EIGHT_BALL_CHALLENGE || this == NINE_BALL_CHALLENGE;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
