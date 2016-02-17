package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Stat;
import com.society.leagues.client.api.domain.Team;
import feign.Param;
import feign.RequestLine;

import java.util.List;

public interface StatApi {

    @RequestLine("GET /api/stat/team/{teamId}/members")
    List<Stat> getTeamMemberStats(@Param("teamId") String teamId);

    /**
     * @see com.society.leagues  getTeamStatsSeason
     * @param seasonId
     * @return
     */
    @RequestLine("GET /api/stat/team/{seasonId}/summary")
    List<Team> teamSeasonStats(@Param("seasonId") String seasonId);

    @RequestLine("GET /api/stat/season/users/{seasonId}/summary")
    List<Stat> getUserSeasonStats(@Param("seasonId") String seasonId);

    @RequestLine("GET /api/stat/user/{userId}/{seasonId}/summary")
    List<Stat> getUserSeasonStats(@Param("userId") String userId, @Param("seasonId") String seasonId);

    @RequestLine("GET /api/stat/user/{userId}/summary")
    List<Stat> getUserStatsSummary(@Param("userId") String userId);
}
