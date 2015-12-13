package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Season;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;

@Headers({"Accept: application/json, */*","Content-Type: application/json","Accept-Encoding: gzip, deflate, sdch" })
public interface SeasonApi {
    @RequestLine("GET /api/season")
    List<Season> get();

    @RequestLine("GET /api/season/active")
    List<Season> active();

    @RequestLine("GET /api/season/{id}")
    Season get(@Param("id") String id);
}
