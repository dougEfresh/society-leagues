package com.society.leagues.client.api.domain;

public enum StatType {
    USER,
    USER_SEASON,
    ALL,
    TEAM,
    HANDICAP_WINS,
    HANDICAP_LOSES,
    MIXED_EIGHT,
    MIXED_NINE,
    LIFETIME_EIGHT_BALL_WEDNESDAY,
    LIFETIME_EIGHT_BALL_THURSDAY,
    LIFETIME_NINE_BALL_TUESDAY,
    LIFETIME_EIGHT_BALL_SCRAMBLE,
    LIFETIME_NINE_BALL_SCRAMBLE,
    UNKNOWN;

    public boolean isLifetime() {
        return this == LIFETIME_EIGHT_BALL_SCRAMBLE ||
                this == LIFETIME_NINE_BALL_TUESDAY ||
                this == LIFETIME_EIGHT_BALL_WEDNESDAY ||
                this == LIFETIME_EIGHT_BALL_THURSDAY ||
                this == LIFETIME_NINE_BALL_SCRAMBLE ||
                this == ALL;
    }
}
