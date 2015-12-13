package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.PlayerResult;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;

@Headers({"Accept: application/json, */*;","Content-Type: application/json", "Accept-Encoding: gzip, deflate, sdch"})
public interface PlayerResultApi {

    @RequestLine("GET /api/playerresult/teammatch/{id}")
    List<PlayerResult> getPlayerResultByTeamMatch(@Param("id") String id);

    @RequestLine("POST /api/playerresult/admin/modify")
    List<PlayerResult> save(List<PlayerResult> results);

    @RequestLine("GET /api/playerresult/admin/delete/{resultId}")
    Boolean delete(@Param("resultId") String resultId);

    @RequestLine("GET /api/playerresult/{teamMatchId}/add")
    List<PlayerResult> add(@Param("teamMatchId") String teamMatchId);
}
