package com.society.leagues.client.api;

public enum Role {
    ADMIN("Root",1),
    OPERATOR("Operator",2),
    Player("Player",3),
    ANON("Anon",4);

    public final String role;
    public final Integer id;

    Role(String role,Integer id) {
        this.role = role;
        this.id = id;
    }

    public static Role fromString(String role) {
        if (ADMIN.role.equals(role))
            return ADMIN;

        if (OPERATOR.role.equals(role))
            return OPERATOR;

          if (Player.role.equals(role))
            return Player;

        return ANON;
    }

    public static boolean isAdmin(Role r) {
        return  r == ADMIN || r == OPERATOR;
    }

    public static Integer id(Role r) {
        return r.id;
    }

    public static Role fromId(Integer id) {
        if (id == null)
            return ANON;

        switch (id) {
            case 1:
                return ADMIN;
            case 2:
                return OPERATOR;
            case 3:
                return Player;
            default:
                return ANON;
        }
    }

    @Override
    public String toString() {
        return role;
    }
}
