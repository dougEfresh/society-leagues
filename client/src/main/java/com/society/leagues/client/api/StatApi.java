package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Team;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;

@Headers({"Accept: application/json, */*;","Content-Type: application/json", "Accept-Encoding: gzip, deflate, sdch"})
public interface StatApi {

    @RequestLine("GET /api/stat/season/{seasonId}")
    public List<Team> getSeasonStats(@Param("seasonId") String seasonId);
}
