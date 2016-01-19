package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Season;
import feign.Param;
import feign.RequestLine;

import java.util.List;


public interface SeasonApi {

    @RequestLine("GET /api/season")
    List<Season> get();

    @RequestLine("GET /api/season/active")
    List<Season> active();

    @RequestLine("GET /api/season/{id}")
    Season get(@Param("id") String id);

    @RequestLine("POST /api/season/admin/create")
    Season create(Season season);

    @RequestLine("POST /api/season/admin/modify")
    Season modify(Season season);
}
