package com.society.leagues.adapters;

import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Status;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.division.Division;

import java.util.HashSet;
import java.util.Set;

public class SeasonAdapter {

    Season season;
    Integer division;
    Set<Integer> teams = new HashSet<>();
    Set<Integer> players = new HashSet<>();

    public SeasonAdapter(Season season, Division division) {
        this.season = season;
        this.division = division.getId();
    }

    public SeasonAdapter() {
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public Integer getDivision() {
        return division;
    }

    public Set<Integer> getTeams() {
        return teams;
    }

    public Set<Integer> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        players.add(player.getId());
    }

    public void addTeam(Team team) {
        teams.add(team.getId());
    }

    public boolean current() {
        return this.season.getSeasonStatus() == Status.ACTIVE;
    }
}
