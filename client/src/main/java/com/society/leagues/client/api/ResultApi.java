package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.PlayerResult;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;
import java.util.Map;

@Headers({"Accept: application/json, */*","Content-Type: application/json","Accept-Encoding: gzip, deflate, sdch" })
public interface ResultApi {

    @RequestLine("GET /api/playerresult/user/{userId}/{seasonId}")
    Map<String,Object> resultsBySeason(@Param("userId") String userId, @Param("seasonId") String seasonId);
}
