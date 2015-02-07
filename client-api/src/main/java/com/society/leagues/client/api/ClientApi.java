package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.LeagueObject;
import com.society.leagues.client.api.domain.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

public interface ClientApi<T extends LeagueObject> {
    
    List<T> get(List<Integer> id);
    
    T get(Integer id);
    
    List<T> get();
}
