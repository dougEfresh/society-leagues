package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.TeamMatch;
import feign.Param;
import feign.RequestLine;

import java.util.List;


public interface SeasonApi {

    @RequestLine("GET /api/season")
    List<Season> get();

    @RequestLine("DELETE /api/season/{seasonId}")
    Season delete(@Param("seasonId") String seasonId);

    @RequestLine("GET /api/season/active")
    List<Season> active();

    @RequestLine("GET /api/season/{id}")
    Season get(@Param("id") String id);

    @RequestLine("POST /api/season/admin/create")
    Season create(Season season);

    @RequestLine("POST /api/season/admin/modify")
    Season modify(Season season);

    @RequestLine("PUT /api/season/create/schedule/{seasonId}")
    List<TeamMatch> schedule(@Param("seasonId") String seasonId);

    @RequestLine("DELETE /api/season/delete/schedule/{seasonId}")
    Season deleteSchedule(@Param("seasonId") String seasonId);
}
