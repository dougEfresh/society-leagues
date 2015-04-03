package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Team;

public interface TeamClientApi extends ClientApi<Team>  {
    Team get(String name);
}
