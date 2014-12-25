package com.society.leagues.domain;

import com.owlike.genson.annotation.JsonConverter;
import com.society.leagues.domain.interfaces.Player;

@SuppressWarnings("unused")
public class DomainUser {
    String username;
    @JsonConverter(PlayerConverter.class)
    Player player;
    String authorities;
    boolean authenticated;
    String token;

    public DomainUser(Player player) {
        this.username = player.getLogin();
        this.player = player;
    }

    public DomainUser(){}

    public String getUsername() {
        return username;
    }

    public int getId() {
        return player.getId();
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public String toString() {
        return String.format("playerId: %s username: %s",
                player == null ? "" : player.getId(),
                username);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

