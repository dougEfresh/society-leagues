package com.society.leagues.domain;

import com.society.leagues.domain.interfaces.Player;

public class DomainUser {
    private String username;
    private Player player;

    public DomainUser(Player player) {
        this.username = player.getLogin();
        this.player = player;
    }

    public String getUsername() {
        return username;
    }

    public int getId() {
        return player.getId();
    }

    @Override
    public String toString() {
        return username;
    }

}
