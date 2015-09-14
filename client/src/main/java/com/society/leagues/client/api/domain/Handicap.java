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

    public static String format(Handicap hc) {
        String h =  hc.name().replace("PLUS","+").replace("PRO","P").replace("OPEN","O").replace("OPEN+","O+");
        if (h.contains("+") || h.length() == 1) {
            return h;
        }
        return h.substring(0,1) + h.substring(1).toLowerCase();
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
            Integer h = new Integer(hc);
            return Handicap.values()[h-1];
        } catch (NumberFormatException e) {

        }
        try {
            return Handicap.valueOf(hc);
        } catch (Throwable r) {

        }
        return Handicap.UNKNOWN;
    }
}
