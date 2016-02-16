package com.society.leagues.client.api.domain;

public enum StatType {
    USER_SEASON,
    ALL,
    MIXED_EIGHT,
    MIXED_NINE,
    MIXED_SCOTCH,
    LIFETIME_EIGHT_BALL_WEDNESDAY,
    LIFETIME_EIGHT_BALL_THURSDAY,
    LIFETIME_NINE_BALL_TUESDAY,
    LIFETIME_EIGHT_BALL_SCRAMBLE,
    LIFETIME_NINE_BALL_SCRAMBLE,
    HANDICAP,
    UNKNOWN;

    public boolean isLifetime() {
        return this == LIFETIME_EIGHT_BALL_SCRAMBLE ||
                this == LIFETIME_NINE_BALL_TUESDAY ||
                this == LIFETIME_EIGHT_BALL_WEDNESDAY ||
                this == LIFETIME_EIGHT_BALL_THURSDAY ||
                this == LIFETIME_NINE_BALL_SCRAMBLE ||
                this == ALL;
    }

    public boolean isScramble() {
        return this == MIXED_EIGHT || this == MIXED_NINE || this == MIXED_SCOTCH;
    }
}
