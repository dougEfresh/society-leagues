package com.society.leagues.client.api.domain;

public enum Division {
    
    EIGHT_BALL_WEDNESDAYS(LeagueType.TEAM,"8ball Weds"),
    EIGHT_BALL_THURSDAYS(LeagueType.TEAM,"8ball Thurs"),
    MIXED_MONDAYS(LeagueType.MIXED,"Mon"),
    NINE_BALL_TUESDAYS(LeagueType.TEAM,"9ball Tues"),
    STRAIGHT(LeagueType.INDIVIDUAL,"Straight"),
    NINE_BALL_CHALLENGE(LeagueType.INDIVIDUAL,"Top Gun");

    final LeagueType leagueType;
    final String displayName;

    Division(LeagueType leagueType,String name) {
        this.leagueType = leagueType; this.displayName = name;
    }

    public boolean isChallenge() {
        return  this == NINE_BALL_CHALLENGE;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
