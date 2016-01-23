package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.client.api.domain.User;
import feign.Param;
import feign.RequestLine;

import java.util.List;
import java.util.Map;

public interface TeamMatchApi {
    @RequestLine("GET /api/teammatch/{id}")
    TeamMatch get(@Param("id") String id);

    @RequestLine("GET /api/teammatch/season/{id}/all")
    @Deprecated
    Map<String,List<TeamMatch>> matchesBySeason(@Param("id") String id);

    @RequestLine("GET /api/teammatch/season/{id}/summary")
    Map<String,List<TeamMatch>> matchesBySeasonSummary(@Param("id") String id);

    @RequestLine("PUT /api/teammatch/admin/add/{seasonId}/{date}")
    TeamMatch add(@Param("seasonId") String seasonId, @Param("date") String date);

    @RequestLine("DELETE /api/teammatch/admin/delete/{matchId}")
    TeamMatch delete(@Param("matchId") String matchId);

    @RequestLine("GET /api/teammatch/members/{id}")
    Map<String,List<User>> teamMembers(@Param("id") String id);

    @RequestLine("GET /api/teammatch/team/{teamId}")
    List<TeamMatch> getTeamMatchByTeam(@Param("teamId") String teamId);

    @RequestLine("POST /api/teammatch/admin/modify/list")
    List<TeamMatch> save(List<TeamMatch> matches);
}
