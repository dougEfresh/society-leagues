package com.society.leagues.adapters;

import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.Role;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.client.api.domain.division.DivisionType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserAdapter {

    User user;
    List<Integer> players = new ArrayList<>();
    Set<Division> divisions = new HashSet<>();
    Set<Integer> teams = new HashSet<>();

    public UserAdapter(User user, List<Player> players) {
        this.user = user;
        for (Player player : players) {
    //        this.players.add(player.getId());
            addDivision(player.getDivision());
            addTeam(player.getTeam());
        }
    }

    public UserAdapter() {
    }

    public String getFirstName() {
        return user.getFirstName();
    }

    public String getLastName() {
        return user.getLastName();
    }

    public String getLogin() {
        return user.getLogin();
    }

    public Role getRole() {
        return user.getRole();
    }

    public List<Integer> getPlayers() {
        return players;
    }

    public String getName() {
        return user.getName();
    }

    public String getEmail() {
        return user.getEmail();
    }

    Set<Division> getDivisions() {
        return divisions;
    }

    public Set<Integer> getTeams() {
        return teams;
    }

    private void addPlayer(Player player) {
        this.players.add(player.getId());
    }

    private void addDivision(Division division) {
        this.divisions.add(division);
    }

    private void addTeam(Team team) {
        this.teams.add(team.getId());
    }

}
