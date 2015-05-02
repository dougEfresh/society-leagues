package com.society.leagues.adapters;

import com.society.leagues.client.api.domain.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserAdapter {

    User user;
    List<Player> players = new ArrayList<>();

    public UserAdapter(User user, List<Player> players) {
        this.user = user;
        this.players = players;
    }

    public UserAdapter() {
    }

    public String getFirstName() {
        return user.getFirstName();
    }

    public String getLastName() {
        return user.getLastName();
    }

    public Role getRole() {
        return user.getRole();
    }

    public Set<Integer> getCurrentSeasons() {
        Set<Integer> ids = new HashSet<>();
        for (Player player : players) {
            if (player.getSeason().getSeasonStatus() == Status.ACTIVE) {
                ids.add(player.getSeason().getId());
            }
        }
        return ids;
    }

    public Set<TeamSeasonAdapter> getCurrentTeams() {
        Set<TeamSeasonAdapter> teams = new HashSet<>();
        for (Player player : players) {
            if (player.getSeason().getSeasonStatus() == Status.ACTIVE) {
                TeamSeasonAdapter team = new TeamSeasonAdapter(player.getTeam(),player.getSeason());
                teams.add(team);
            }
        }
        return teams;
    }
}
