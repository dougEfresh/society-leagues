package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.TeamMembers;
import feign.Param;
import feign.RequestLine;

import java.util.List;

public interface TeamApi {

    @RequestLine("GET /api/team/season/{id}")
    List<Team> seasonTeams(@Param("id") String id);

    @RequestLine("GET /api/team/active")
    List<Team> active();

    @RequestLine("GET /api/team/{id}")
    Team get(@Param("id") String id);

    @RequestLine("GET /api/team/{teamId}/members")
    TeamMembers members(@Param("teamId") String teamId);

    @RequestLine("GET /api/team/user/{userId}")
    List<Team> userTeams(@Param("userId") String userId);

    @RequestLine("POST /api/team/admin/modify")
    Team save(Team team);

    @RequestLine("DELETE /api/team/admin/delete/{teamId}")
    Team delete(@Param("teamId") String teamId);

    @RequestLine("POST /api/team/admin/modify/members/{teamId}")
    TeamMembers saveMembers(@Param("teamId") String teamId, TeamMembers members);
}
