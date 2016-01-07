
package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.PlayerResult;
import feign.Param;
import feign.RequestLine;

import java.util.List;

public interface PlayerResultApi {

    @RequestLine("GET /api/playerresult/{userId}/{seasonId}")
    List<PlayerResult> getResults(@Param("userId") String userId, @Param("seasonId") String seasonId);

    @RequestLine("GET /api/playerresult/teammatch/{teamId}")
    List<PlayerResult> getPlayerResultByTeamMatch(@Param("teamId") String teamId);

    @RequestLine("GET /api/playerresult/teammatch/{teamId}/all")
    List<PlayerResult> getPlayerResults(@Param("teamId") String teamId);

    @RequestLine("POST /api/playerresult/admin/modify")
    List<PlayerResult> save(List<PlayerResult> results);

    @RequestLine("DELETE /api/playerresult/admin/delete/{resultId}")
    Boolean delete(@Param("resultId") String resultId);

    @RequestLine("PUT /api/playerresult/{teamMatchId}/add")
    List<PlayerResult> add(@Param("teamMatchId") String teamMatchId);
}
