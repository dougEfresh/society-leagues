package com.society.admin.model;

import com.society.leagues.client.api.domain.PlayerResult;
import com.society.leagues.client.api.domain.TeamMatch;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;

public class MatchModel extends TeamMatch {

    List<PlayerResult> playerResults = new ArrayList<>();

    public static List<MatchModel> fromTeam(List<TeamMatch> teamMatches) {
        List<MatchModel> matchModels = new ArrayList<>(teamMatches.size());

        for (TeamMatch teamMatch : teamMatches) {
            MatchModel m = new MatchModel();
            ReflectionUtils.shallowCopyFieldState(teamMatch,m);
            matchModels.add(m);
        }

        return matchModels;
    }

    public List<PlayerResult> getPlayerResults() {
        return playerResults;
    }

    public void setPlayerResults(List<PlayerResult> playerResults) {
        this.playerResults = playerResults;
    }
}
