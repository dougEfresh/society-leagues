package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Team;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;

@Headers({"Accept: application/json","Content-Type: application/json"})
public interface TeamApi {

    @RequestLine("GET /api/team/season/{id}")
    List<Team> getBySeason(@Param("id") String id);
}
