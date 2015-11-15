package com.society.leagues.client.api.domain;

public enum Division {
    
    EIGHT_BALL_WEDNESDAYS(LeagueType.TEAM,"8ball Weds"),
    EIGHT_BALL_THURSDAYS(LeagueType.TEAM,"8ball Thurs"),
    MIXED_MONDAYS_MIXED(LeagueType.MIXED,"Scramble"),
    NINE_BALL_TUESDAYS(LeagueType.TEAM,"9ball Tues"),
    STRAIGHT(LeagueType.INDIVIDUAL,"Straight"),
    MIXED_EIGHT(LeagueType.MIXED,"Scramble 8"),
    MIXED_NINE(LeagueType.MIXED,"Scramble 9"),
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
