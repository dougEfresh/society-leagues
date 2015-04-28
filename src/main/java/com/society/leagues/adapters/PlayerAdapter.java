package com.society.leagues.adapters;

import com.society.leagues.client.api.domain.Handicap;
import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Status;
import com.society.leagues.client.api.domain.division.DivisionType;

import java.util.Date;

public class PlayerAdapter {

    Player player;

    public PlayerAdapter(Player player) {
        this.player = player;
    }

    public PlayerAdapter() {
    }

    public Integer getSeason() {
        return player.getSeason().getId();
    }

    public Integer getDivision() {return player.getDivision().getId();}

    public Integer getTeam() {return player.getTeam().getId();}

    public Handicap getHandicap() { return player.getHandicap();}

    public Status status() {
        return player.getEnd() == null ? Status.INACTIVE : Status.ACTIVE;
    }

}
