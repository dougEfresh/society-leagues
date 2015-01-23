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
    public List<Team> current(List<User> users) {
        return  dao.current(users);
    }

    @Override
    public List<Team> current(Integer userId) {
        return dao.current(userId);
    }

    @Override
    public List<Team> past(List<User> user) {
        return dao.past(user);
    }

    @Override
    public List<Team> past(Integer userId) {
        return dao.past(userId);
    }

    @Override
    public List<Team> all(List<User> user) {
        return dao.all(user);
    }

    @Override
    public List<Team> all(Integer userId) {
        return dao.all(userId);
    }

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
        return null;
    }
}
