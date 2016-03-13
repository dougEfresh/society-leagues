package com.society.leagues.model;

import com.society.leagues.CopyUtil;
import com.society.leagues.client.api.domain.Team;

import java.util.Collection;
import java.util.List;

public class TeamStat extends Team {

    public static List<TeamStat> teamStatsFromTeam(Collection<Team> teams) {
        return CopyUtil.copy(teams, TeamStat.class);
    }
}
