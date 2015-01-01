package com.society.leagues.infrastructure.security;

import java.util.ArrayList;
import java.util.List;

public class PrincipalToken {

    final String token;
    final String user;
    final List<String> roles = new ArrayList<>();

    public PrincipalToken(String token, String user) {
        this.token = token;
        this.user = user;
    }

    public void addRole(String role) {
        roles.add(role);
    }

    public String getToken() {
        return token;
    }

    public String getUser() {
        return user;
    }
}
