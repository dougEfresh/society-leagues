package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.LeagueObject;

import java.util.Collection;
import java.util.List;

public interface ClientApi<T extends LeagueObject> {
    
    List<T> get(List<Integer> id);
    
    T get(Integer id);
    
    Collection<T> get();
}
