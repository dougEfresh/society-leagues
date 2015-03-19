package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.TeamResult;

public interface TeamResultApi extends ClientApi<TeamResult> {
    TeamResult getByMatch(Integer id);
}
