package com.society.leagues.client.api.domain;

public enum Handicap {
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    TEN,
    ELEVEN,
    TWELVE,
    THIRTEEN,
    FOURTEEN,
    FIFTEEN,
    SIXTEEN,
    SEVENTEEN,
    D,
    DPLUS,
    C,
    CPLUS,
    B,
    BPLUS,
    A,
    APLUS,
    OPEN,
    OPENPLUS,
    PRO;

    public static Handicap get(String hc) {
        if (hc == null) {
            return null;
        }
        hc = hc.replace("+","PLUS").toUpperCase();
        try {
            Integer h = new Integer(hc);
            return Handicap.values()[h-1];
        } catch (NumberFormatException e) {

        }
        try {
            return Handicap.valueOf(hc);
        } catch (Throwable r) {

        }
        return null;
    }
}
