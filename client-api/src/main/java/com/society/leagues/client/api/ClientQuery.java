package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.LeagueObject;
import com.society.leagues.client.api.domain.filter.Filter;


import java.util.List;


public interface ClientQuery<T extends LeagueObject> {
    List<T> get(Filter filter);
}
