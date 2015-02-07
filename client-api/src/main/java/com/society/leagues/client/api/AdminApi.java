package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.LeagueObject;

public interface AdminApi<Q extends LeagueObject> {

    Q create(final Q object);

    Boolean delete(final Q object);

    Q modify(Q division);
}
