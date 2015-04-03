package com.society.leagues.client.api.domain.division;

public enum DivisionType {
    
    EIGHT_BALL_WEDNESDAYS(LeagueType.TEAM),
    EIGHT_BALL_THURSDAYS(LeagueType.TEAM),
    EIGHT_BALL_MIXED_MONDAYS(LeagueType.TEAM),
    NINE_BALL_MIXED_MONDAYS(LeagueType.TEAM),
    NINE_BALL_TUESDAYS(LeagueType.TEAM),
    EIGHT_BALL_CHALLENGE(LeagueType.INDIVIDUAL),
    NINE_BALL_CHALLENGE(LeagueType.INDIVIDUAL);

    final LeagueType leagueType;

    DivisionType(LeagueType leagueType) {
        this.leagueType = leagueType;
    }

    public LeagueType getLeagueType() {
        return leagueType;
    }

    @Override
    public String toString() {
        return "DivisionType{" +
                "DivisionType=" + this.name() +
                "  leagueType=" + leagueType +
                '}';
    }
}
