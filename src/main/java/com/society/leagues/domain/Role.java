package com.society.leagues.domain;

public enum Role {
    ROOT("Root"),
    OPERATOR("Operator"),
    PLAYER("Player");

    final String role;
    Role(String role) {
        this.role = role;
    }

    public static Role fromString(String role) {
        if (ROOT.role.equals(role))
            return ROOT;

        if (OPERATOR.role.equals(role))
            return OPERATOR;

        return PLAYER;
    }

    public static boolean isAdmin(Role r) {
        return  r == ROOT || r == OPERATOR;
    }
}
