package com.society.leagues.client.api.domain;

public enum Division {
    
    EIGHT_BALL_WEDNESDAYS(LeagueType.TEAM,"8ball Weds",5,"wednesday"),
    EIGHT_BALL_THURSDAYS(LeagueType.TEAM,"8ball Thurs",6,"thursday"),
    MIXED_MONDAYS_MIXED(LeagueType.MIXED,"Scramble",1,"monday"),
    NINE_BALL_TUESDAYS(LeagueType.TEAM,"9ball Tues",3,"tuesday"),
    STRAIGHT(LeagueType.INDIVIDUAL,"Straight",99,""),
    MIXED_EIGHT(LeagueType.MIXED,"Scramble 8",2,"monday"),
    MIXED_NINE(LeagueType.MIXED,"Scramble 9",1,"monday"),
    UNKNOWN(LeagueType.INDIVIDUAL,"Unknown",100,""),
    NINE_BALL_CHALLENGE(LeagueType.INDIVIDUAL,"Top Gun",0,"sunday");

    final LeagueType leagueType;
    final String displayName;
    final Integer order;
    final String day;

    Division(LeagueType leagueType,String name,int order, String day) {
        this.leagueType = leagueType; this.displayName = name; this.order = order; this.day = day;
    }

    public boolean isChallenge() {
        return  this == NINE_BALL_CHALLENGE;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
