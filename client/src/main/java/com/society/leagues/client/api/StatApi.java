package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Stat;
import com.society.leagues.client.api.domain.Team;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;

@Headers({"Accept: application/json, */*;","Content-Type: application/json", "Accept-Encoding: gzip, deflate, sdch"})
public interface StatApi {

    @RequestLine("GET /api/stat/team/{teamId}/members")
    List<Stat> getTeamMemberStats(@Param("teamId") String teamId);

    @RequestLine("GET /api/stat/season/{seasonId}")
    List<Team> getSeasonStats(@Param("seasonId") String seasonId);

    @RequestLine("GET /api/stat/season/players/{seasonId}")
    List<Stat> getUsersSeasonStats(@Param("seasonId") String seasonId);

    @RequestLine("GET /api/stat/user/{userId}/all")
    List<Stat> getUserStats(@Param("userId") String userId);
}
