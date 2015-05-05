package com.society.leagues.adapters;

import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Team;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class TeamAdapter {
    Team team;
    List<Season> seasons;

    public TeamAdapter(Team team, List<Season> seasons) {
        this.seasons = seasons;
        this.team = team;
    }

    public TeamAdapter() {
    }

    public Integer getId() {
        return team.getId();
    }

    public Set<Integer> getSeasons(){
        Set<Integer> hashSet = new HashSet<>();
         hashSet.addAll(seasons.stream().map(Season::getId).collect(Collectors.toList()));
        return hashSet;
    }

    public String getName() {
        return team.getName();
    }
}
