package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.client.api.domain.User;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;
import java.util.Map;

@Headers({"Accept: application/json","Content-Type: application/json"})
public interface TeamMatchApi {

    @RequestLine("GET /api/teammatch/season/{id}/all")
    Map<String,List<TeamMatch>> matchesBySeason(@Param("id") String id);

    @RequestLine("GET /api/teammatch/admin/add/{seasonId}/{date}")
    Map<String,List<TeamMatch>> add(@Param("seasonId") String seasonId, @Param("date") String date);

    @RequestLine("GET /api/teammatch/members/{id}")
    Map<String,List<User>> teamMembers(@Param("id") String id);

    @RequestLine("POST /api/teammatch/admin/modify/list")
    List<TeamMatch> save(List<TeamMatch> matches);
}
