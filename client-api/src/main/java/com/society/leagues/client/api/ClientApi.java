package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.LeagueObject;
import com.society.leagues.client.api.domain.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

public interface ClientApi<T extends LeagueObject> {
    
    List<T> current(List<User> users);

    List<T> current(Integer userId);

    List<T> past(List<User> user);

    List<T> past(Integer userId);
    
    List<T> all(List<User> user);

    List<T> all(Integer userId);
    
    T get(Integer id);
    
    List<T> get();
}
