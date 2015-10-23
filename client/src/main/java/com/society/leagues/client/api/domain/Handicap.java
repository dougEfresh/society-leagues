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
    PRO,
    UNKNOWN;

    public static boolean isNine(Handicap hc) {
        return hc.ordinal() >= 10;
    }

    public static Handicap get(String hc) {
        if (hc == null) {
            return Handicap.UNKNOWN;
        }
        hc = hc.replace("+","PLUS").toUpperCase();
        if (hc.equals("O")) {
            return OPEN;
        }

        if (hc.equals("P")) {
            return PRO;
        }
        try {
            Integer h = new Integer(hc)-1;
            if (h>0 && h < Handicap.values().length)
                return Handicap.values()[h];
        } catch (NumberFormatException e) {

        }
        try {
            return Handicap.valueOf(hc);
        } catch (Throwable r) {

        }
        return Handicap.UNKNOWN;
    }
}
