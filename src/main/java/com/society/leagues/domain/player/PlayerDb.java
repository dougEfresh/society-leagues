package com.society.leagues.domain.player;

import com.society.leagues.domain.interfaces.Player;

import java.util.Map;

public class PlayerDb implements Player {

    Map<String,Object> data;

    public PlayerDb() {
    }

    public PlayerDb(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public int getId() {
        if (data == null || data.isEmpty() || data.get("player_id") == null)
            return -1;

        return  Integer.parseInt(data.get("player_id").toString());
    }

    @Override
    public String getLogin() {
        return null;
    }

    @Override
    public String getFirstName() {
        return null;
    }

    @Override
    public String getLastName() {
        return null;
    }
}
