package com.society.leagues.client.api.domain;

public class Creds {
    String username;
    String password;
    boolean springRememberMe = true;

    public Creds() {
    }

    public Creds(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSpringRememberMe() {
        return springRememberMe;
    }

    public void setSpringRememberMe(boolean springRememberMe) {
        this.springRememberMe = springRememberMe;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
