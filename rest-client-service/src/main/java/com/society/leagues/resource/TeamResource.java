package com.society.leagues.resource;

import com.society.leagues.client.api.TeamClientApi;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.dao.TeamDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@SuppressWarnings("unused")
public class TeamResource extends ApiResource implements TeamClientApi {
    
    @Autowired TeamDao dao;
    
    @Override
    public Team get(Integer id) {
        return dao.get(id);
    }

    @Override
    public List<Team> get() {
        return dao.get();
    }

    @Override
    public Team get(String name) {
        return dao.get(name);
    }

    @Override
    public List<Team> get(List<Integer> id) {
        return dao.get(id);
    }
}
