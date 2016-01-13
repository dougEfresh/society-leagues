package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Stat;
import com.society.leagues.client.api.domain.Team;
import feign.Param;
import feign.RequestLine;

import java.util.List;

public interface StatApi {

    @RequestLine("GET /api/stat/team/{teamId}/members")
    List<Stat> getTeamMemberStats(@Param("teamId") String teamId);

    @RequestLine("GET /api/stat/season/{seasonId}")
    List<Team> getSeasonStats(@Param("seasonId") String seasonId);

    @RequestLine("GET /api/stat/team/{seasonId}/summary")
    List<Team> getTeamSeasonStats(@Param("seasonId") String seasonId);

    @RequestLine("GET /api/stat/season/users/{seasonId}/summary")
    List<Stat> getUserSeasonStats(@Param("seasonId") String seasonId);

    @RequestLine("GET /api/stat/season/players/{seasonId}")
    @Deprecated
    List<Stat> getUsersSeasonStats(@Param("seasonId") String seasonId);

    @RequestLine("GET /api/stat/user/{userId}/all")
    @Deprecated
    List<Stat> getUserStats(@Param("userId") String userId);

    @RequestLine("GET /api/stat/user/{userId}/{seasonId}/summary")
    List<Stat> getUserSeasonStats(@Param("userId") String userId, @Param("seasonId") String seasonId);

    @RequestLine("GET /api/stat/user/{userId}")
    @Deprecated
    List<Stat> getUserStatsActive(@Param("userId") String userId);

    @RequestLine("GET /api/stat/user/{userId}/summary")
    List<Stat> getUserStatsSummary(@Param("userId") String userId);
}
