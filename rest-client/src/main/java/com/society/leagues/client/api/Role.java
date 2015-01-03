package com.society.leagues.client.api;

public enum Role {
    ADMIN("Root"),
    OPERATOR("Operator"),
    PLAYER("Player");

    final String role;
    Role(String role) {
        this.role = role;
    }

    public static Role fromString(String role) {
        if (ADMIN.role.equals(role))
            return ADMIN;

        if (OPERATOR.role.equals(role))
            return OPERATOR;

        return PLAYER;
    }

    public static boolean isAdmin(Role r) {
        return  r == ADMIN || r == OPERATOR;
    }
}
