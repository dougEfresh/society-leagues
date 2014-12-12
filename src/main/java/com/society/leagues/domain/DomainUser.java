package com.society.leagues.domain;

import com.society.leagues.domain.objects.Player;

public class DomainUser {
    private String username;
    private int playerId;

    public DomainUser(String username, int playerId) {
        this.playerId = playerId;
        this.username = username;
    }

    public DomainUser(Player player) {
        this.username = player.getLogin();
        this.playerId = player.getId();
    }

    public String getUsername() {
        return username;
    }

    public int getId() {
        return playerId;
    }

    @Override
    public String toString() {
        return username;
    }

}

