package com.society.leagues.adapters;

import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Team;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class TeamAdapter {
    Team team;
    List<Player> players;

    public TeamAdapter(Team team,  List<Player> players) {
        this.team = team;
        this.players = players;
    }

    public TeamAdapter() {
    }

    public Integer getTeamId() {
        return team.getId();
    }

    public Map<Integer,List<Integer>> getSeasons(){
        Map<Integer,List<Integer>> seasons = new HashMap<>();
        for (Player player : players.stream().filter(p->p.getTeam().equals(team)).collect(Collectors.toList())) {
            if (!seasons.containsKey(player.getSeason().getId())) {
                seasons.put(player.getSeason().getId(),new ArrayList<>());
            }
            seasons.get(player.getSeason().getId()).add(player.getUserId());
        }
        return seasons;
    }

    public String getName() {
        return team.getName();
    }
}
