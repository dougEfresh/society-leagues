package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.TeamMembers;
import com.society.leagues.client.api.domain.User;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;

@Headers({"Accept: application/json, */*","Content-Type: application/json", "Accept-Encoding: gzip, deflate, sdch"})
public interface TeamApi {

    @RequestLine("GET /api/team/season/{id}")
    List<Team> getBySeason(@Param("id") String id);

    @RequestLine("GET /api/team/active")
    List<Team> active();

    @RequestLine("GET /api/team/{id}")
    Team get(@Param("id") String id);

    @RequestLine("GET /api/team/{teamId}/members")
    List<User> members(@Param("teamId") String teamId);

    @RequestLine("GET /api/team/admin/modify")
    Team save(Team team);
}
