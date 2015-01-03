package com.society.leagues.client.api;

public enum Role {
    ADMIN("Root"),
    OPERATOR("Operator"),
    USER("Player"),
    ANON("Anon");

    final String role;
    Role(String role) {
        this.role = role;
    }

    public static Role fromString(String role) {
        if (ADMIN.role.equals(role))
            return ADMIN;

        if (OPERATOR.role.equals(role))
            return OPERATOR;

          if (USER.role.equals(role))
            return USER;

        return ANON;
    }

    public static boolean isAdmin(Role r) {
        return  r == ADMIN || r == OPERATOR;
    }

    @Override
    public String toString() {
        return role;
    }
}
