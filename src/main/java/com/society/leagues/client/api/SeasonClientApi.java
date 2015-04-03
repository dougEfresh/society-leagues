package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Season;

public interface SeasonClientApi extends ClientApi<Season> {
    Season get(String name);
}
