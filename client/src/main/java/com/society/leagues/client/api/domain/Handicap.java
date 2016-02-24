package com.society.leagues.client.api.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Handicap {
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("10"),
    ELEVEN("11"),
    TWELVE("12"),
    THIRTEEN("13"),
    FOURTEEN("14"),
    FIFTEEN("15"),
    SIXTEEN("16"),
    SEVENTEEN("17"),
    D("D"),
    DPLUS("D+"),
    C("C"),
    CPLUS("C+"),
    B("B"),
    BPLUS("B+"),
    A("A"),
    APLUS("A+"),
    OPEN("O"),
    OPENPLUS("O+"),
    PRO("P"),
    UNKNOWN("UNK"),
    NA("NA");

    final String displayName;

    public String getDisplayName() {
        return displayName;
    }

    Handicap(String displayName) {
        this.displayName = displayName;
    }

    public static String format(Handicap hc) {
        if (hc == null || hc == UNKNOWN) {
            return "NA";
        }
        return hc.displayName;
    }

    public static boolean isNine(Handicap hc) {
        return hc.ordinal() >= 10;
    }
    public static Handicap get(String hc) {
        if (hc == null) {
            return Handicap.UNKNOWN;
        }
        hc = hc.replace("+", "PLUS").toUpperCase();
        if (hc.equals("O")) {
            return OPEN;
        }

        if (hc.equals("P")) {
            return PRO;
        }
        try {
            Integer h = new Integer(hc) - 1;
            if (h > 0 && h < Handicap.values().length)
                return Handicap.values()[h];
        } catch (NumberFormatException ignore) {

        }
        try {
            return Handicap.valueOf(hc);
        } catch (Throwable ignore) {

        }
        return Handicap.UNKNOWN;
    }

    public String getValue() {
        return name();
    }

    public static String[][] raceChart = new String[Handicap.values().length+1][Handicap.values().length+1];

    public static String race(Handicap hc1, Handicap hc2) {
        if (hc1 == null || hc1 == UNKNOWN || hc2 == null || hc2 == UNKNOWN)
            return "";

        if ( (!Handicap.isNine(hc1)) || (!Handicap.isNine(hc2))) {
            return "";
        }

        try {
            return raceChart[hc1.ordinal()][hc2.ordinal()];
        } catch (Throwable t) {

        }
        return "";
    }

    static {
        for (int i = 0; i <= Handicap.values().length; i++) {
            for (int j = 0; j <= Handicap.values().length; j++) {
                raceChart[i][j] = "0";
            }
        }
        raceChart[Handicap.D.ordinal()][Handicap.D.ordinal()] = "0/7";
        raceChart[Handicap.D.ordinal()][Handicap.DPLUS.ordinal()] = "1/7";
        raceChart[Handicap.D.ordinal()][Handicap.C.ordinal()] = "2/7";
        raceChart[Handicap.D.ordinal()][Handicap.CPLUS.ordinal()] = "3/8";
        raceChart[Handicap.D.ordinal()][Handicap.B.ordinal()] = "4/9";
        raceChart[Handicap.D.ordinal()][Handicap.BPLUS.ordinal()] = "5/10";
        raceChart[Handicap.D.ordinal()][Handicap.A.ordinal()] = "6/11";
        raceChart[Handicap.D.ordinal()][Handicap.APLUS.ordinal()] = "7/11";
        raceChart[Handicap.D.ordinal()][Handicap.OPEN.ordinal()] = "7/10";
        raceChart[Handicap.D.ordinal()][Handicap.OPENPLUS.ordinal()] = "8/10";
        raceChart[Handicap.D.ordinal()][Handicap.PRO.ordinal()] = "9/11";

        raceChart[Handicap.DPLUS.ordinal()][Handicap.D.ordinal()] = "1/7";
        raceChart[Handicap.DPLUS.ordinal()][Handicap.DPLUS.ordinal()] = "0/7";
        raceChart[Handicap.DPLUS.ordinal()][Handicap.C.ordinal()] = "1/7";
        raceChart[Handicap.DPLUS.ordinal()][Handicap.CPLUS.ordinal()] = "2/7";
        raceChart[Handicap.DPLUS.ordinal()][Handicap.B.ordinal()] = "3/8";
        raceChart[Handicap.DPLUS.ordinal()][Handicap.BPLUS.ordinal()] = "4/9";
        raceChart[Handicap.DPLUS.ordinal()][Handicap.A.ordinal()] = "5/10";
        raceChart[Handicap.DPLUS.ordinal()][Handicap.APLUS.ordinal()] = "6/10";
        raceChart[Handicap.DPLUS.ordinal()][Handicap.OPEN.ordinal()] = "7/11";
        raceChart[Handicap.DPLUS.ordinal()][Handicap.OPENPLUS.ordinal()] = "7/10";
        raceChart[Handicap.DPLUS.ordinal()][Handicap.PRO.ordinal()] = "8/10";

        raceChart[Handicap.C.ordinal()][Handicap.D.ordinal()] = "2/7";
        raceChart[Handicap.C.ordinal()][Handicap.DPLUS.ordinal()] = "1/7";
        raceChart[Handicap.C.ordinal()][Handicap.C.ordinal()] = "0/7";
        raceChart[Handicap.C.ordinal()][Handicap.CPLUS.ordinal()] = "1/7";
        raceChart[Handicap.C.ordinal()][Handicap.B.ordinal()] = "2/7";
        raceChart[Handicap.C.ordinal()][Handicap.BPLUS.ordinal()] = "3/8";
        raceChart[Handicap.C.ordinal()][Handicap.A.ordinal()] = "4/9";
        raceChart[Handicap.C.ordinal()][Handicap.APLUS.ordinal()] = "5/10";
        raceChart[Handicap.C.ordinal()][Handicap.OPEN.ordinal()] = "6/11";
        raceChart[Handicap.C.ordinal()][Handicap.OPENPLUS.ordinal()] = "7/11";
        raceChart[Handicap.C.ordinal()][Handicap.PRO.ordinal()] = "8/11";

        raceChart[Handicap.CPLUS.ordinal()][Handicap.D.ordinal()] = "3/8";
        raceChart[Handicap.CPLUS.ordinal()][Handicap.DPLUS.ordinal()] = "2/7";
        raceChart[Handicap.CPLUS.ordinal()][Handicap.C.ordinal()] = "1/7";
        raceChart[Handicap.CPLUS.ordinal()][Handicap.CPLUS.ordinal()] = "0/7";
        raceChart[Handicap.CPLUS.ordinal()][Handicap.B.ordinal()] = "1/7";
        raceChart[Handicap.CPLUS.ordinal()][Handicap.BPLUS.ordinal()] = "2/7";
        raceChart[Handicap.CPLUS.ordinal()][Handicap.A.ordinal()] = "3/8";
        raceChart[Handicap.CPLUS.ordinal()][Handicap.APLUS.ordinal()] = "4/9";
        raceChart[Handicap.CPLUS.ordinal()][Handicap.OPEN.ordinal()] = "5/10";
        raceChart[Handicap.CPLUS.ordinal()][Handicap.OPENPLUS.ordinal()] = "6/10";
        raceChart[Handicap.CPLUS.ordinal()][Handicap.PRO.ordinal()] = "7/11";

        raceChart[Handicap.B.ordinal()][Handicap.D.ordinal()] = "4/9";
        raceChart[Handicap.B.ordinal()][Handicap.DPLUS.ordinal()] = "3/8";
        raceChart[Handicap.B.ordinal()][Handicap.C.ordinal()] = "2/7";
        raceChart[Handicap.B.ordinal()][Handicap.CPLUS.ordinal()] = "1/7";
        raceChart[Handicap.B.ordinal()][Handicap.B.ordinal()] = "0/7";
        raceChart[Handicap.B.ordinal()][Handicap.BPLUS.ordinal()] = "1/7";
        raceChart[Handicap.B.ordinal()][Handicap.A.ordinal()] = "2/7";
        raceChart[Handicap.B.ordinal()][Handicap.APLUS.ordinal()] = "3/8";
        raceChart[Handicap.B.ordinal()][Handicap.OPEN.ordinal()] = "4/9";
        raceChart[Handicap.B.ordinal()][Handicap.OPENPLUS.ordinal()] = "5/10";
        raceChart[Handicap.B.ordinal()][Handicap.PRO.ordinal()] = "6/10";

        raceChart[Handicap.BPLUS.ordinal()][Handicap.D.ordinal()] = "5/10";
        raceChart[Handicap.BPLUS.ordinal()][Handicap.DPLUS.ordinal()] = "4/9";
        raceChart[Handicap.BPLUS.ordinal()][Handicap.C.ordinal()] = "3/8";
        raceChart[Handicap.BPLUS.ordinal()][Handicap.CPLUS.ordinal()] = "2/7";
        raceChart[Handicap.BPLUS.ordinal()][Handicap.B.ordinal()] = "1/7";
        raceChart[Handicap.BPLUS.ordinal()][Handicap.BPLUS.ordinal()] = "0/7";
        raceChart[Handicap.BPLUS.ordinal()][Handicap.A.ordinal()] = "1/7";
        raceChart[Handicap.BPLUS.ordinal()][Handicap.APLUS.ordinal()] = "2/7";
        raceChart[Handicap.BPLUS.ordinal()][Handicap.OPEN.ordinal()] = "3/8";
        raceChart[Handicap.BPLUS.ordinal()][Handicap.OPENPLUS.ordinal()] = "4/9";
        raceChart[Handicap.BPLUS.ordinal()][Handicap.PRO.ordinal()] = "5/10";

        raceChart[Handicap.A.ordinal()][Handicap.D.ordinal()] = "6/11";
        raceChart[Handicap.A.ordinal()][Handicap.DPLUS.ordinal()] = "5/10";
        raceChart[Handicap.A.ordinal()][Handicap.C.ordinal()] = "4/9";
        raceChart[Handicap.A.ordinal()][Handicap.CPLUS.ordinal()] = "3/8";
        raceChart[Handicap.A.ordinal()][Handicap.B.ordinal()] = "2/8";
        raceChart[Handicap.A.ordinal()][Handicap.BPLUS.ordinal()] = "1/8";
        raceChart[Handicap.A.ordinal()][Handicap.A.ordinal()] = "0/8";
        raceChart[Handicap.A.ordinal()][Handicap.APLUS.ordinal()] = "1/8";
        raceChart[Handicap.A.ordinal()][Handicap.OPEN.ordinal()] = "2/8";
        raceChart[Handicap.A.ordinal()][Handicap.OPENPLUS.ordinal()] = "2/9";
        raceChart[Handicap.A.ordinal()][Handicap.PRO.ordinal()] = "3/9";

        raceChart[Handicap.APLUS.ordinal()][Handicap.D.ordinal()] = "7/11";
        raceChart[Handicap.APLUS.ordinal()][Handicap.DPLUS.ordinal()] = "6/10";
        raceChart[Handicap.APLUS.ordinal()][Handicap.C.ordinal()] = "5/10";
        raceChart[Handicap.APLUS.ordinal()][Handicap.CPLUS.ordinal()] = "4/9";
        raceChart[Handicap.APLUS.ordinal()][Handicap.B.ordinal()] = "3/8";
        raceChart[Handicap.APLUS.ordinal()][Handicap.BPLUS.ordinal()] = "2/8";
        raceChart[Handicap.APLUS.ordinal()][Handicap.A.ordinal()] = "1/8";
        raceChart[Handicap.APLUS.ordinal()][Handicap.APLUS.ordinal()] = "0/8";
        raceChart[Handicap.APLUS.ordinal()][Handicap.OPEN.ordinal()] = "1/9";
        raceChart[Handicap.APLUS.ordinal()][Handicap.OPENPLUS.ordinal()] = "2/9";
        raceChart[Handicap.APLUS.ordinal()][Handicap.PRO.ordinal()] = "4/9";

        raceChart[Handicap.OPEN.ordinal()][Handicap.D.ordinal()] = "7/10";
        raceChart[Handicap.OPEN.ordinal()][Handicap.DPLUS.ordinal()] = "7/11";
        raceChart[Handicap.OPEN.ordinal()][Handicap.C.ordinal()] = "6/11";
        raceChart[Handicap.OPEN.ordinal()][Handicap.CPLUS.ordinal()] = "5/10";
        raceChart[Handicap.OPEN.ordinal()][Handicap.B.ordinal()] = "4/9";
        raceChart[Handicap.OPEN.ordinal()][Handicap.BPLUS.ordinal()] = "3/10";
        raceChart[Handicap.OPEN.ordinal()][Handicap.A.ordinal()] = "2/9";
        raceChart[Handicap.OPEN.ordinal()][Handicap.APLUS.ordinal()] = "1/9";
        raceChart[Handicap.OPEN.ordinal()][Handicap.OPEN.ordinal()] = "0/9";
        raceChart[Handicap.OPEN.ordinal()][Handicap.OPENPLUS.ordinal()] = "1/9";
        raceChart[Handicap.OPEN.ordinal()][Handicap.PRO.ordinal()] = "3/10";

        raceChart[Handicap.OPENPLUS.ordinal()][Handicap.D.ordinal()] = "8/10";
        raceChart[Handicap.OPENPLUS.ordinal()][Handicap.DPLUS.ordinal()] = "7/10";
        raceChart[Handicap.OPENPLUS.ordinal()][Handicap.C.ordinal()] = "7/11";
        raceChart[Handicap.OPENPLUS.ordinal()][Handicap.CPLUS.ordinal()] = "6/11";
        raceChart[Handicap.OPENPLUS.ordinal()][Handicap.B.ordinal()] = "5/10";
        raceChart[Handicap.OPENPLUS.ordinal()][Handicap.BPLUS.ordinal()] = "4/9";
        raceChart[Handicap.OPENPLUS.ordinal()][Handicap.A.ordinal()] = "3/10";
        raceChart[Handicap.OPENPLUS.ordinal()][Handicap.APLUS.ordinal()] = "2/9";
        raceChart[Handicap.OPENPLUS.ordinal()][Handicap.OPEN.ordinal()] = "1/";
        raceChart[Handicap.OPENPLUS.ordinal()][Handicap.OPENPLUS.ordinal()] = "0/9";
        raceChart[Handicap.OPENPLUS.ordinal()][Handicap.PRO.ordinal()] = "2/10";

        raceChart[Handicap.PRO.ordinal()][Handicap.D.ordinal()] = "9/11";
        raceChart[Handicap.PRO.ordinal()][Handicap.DPLUS.ordinal()] = "8/10";
        raceChart[Handicap.PRO.ordinal()][Handicap.C.ordinal()] = "8/11";
        raceChart[Handicap.PRO.ordinal()][Handicap.CPLUS.ordinal()] = "6/10";
        raceChart[Handicap.PRO.ordinal()][Handicap.B.ordinal()] = "6/11";
        raceChart[Handicap.PRO.ordinal()][Handicap.BPLUS.ordinal()] = "5/10";
        raceChart[Handicap.PRO.ordinal()][Handicap.A.ordinal()] = "4/10";
        raceChart[Handicap.PRO.ordinal()][Handicap.APLUS.ordinal()] = "3/9";
        raceChart[Handicap.PRO.ordinal()][Handicap.OPEN.ordinal()] = "3/10";
        raceChart[Handicap.PRO.ordinal()][Handicap.OPENPLUS.ordinal()] = "2/10";
        raceChart[Handicap.PRO.ordinal()][Handicap.PRO.ordinal()] = "0/9";
    }
}