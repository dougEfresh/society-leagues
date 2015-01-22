package com.society.leagues.resource;

import com.society.leagues.client.api.PlayerClientApi;
import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.dao.PlayerDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;


@Component
@SuppressWarnings("unused")
public class PlayerResource extends ApiResource implements PlayerClientApi {
    @Autowired PlayerDao dao;
        
    @Override
    public List<Player> current(List<User> users) {
        return  dao.current(users);
    }

    @Override
    public List<Player> current(Integer userId) {
        return dao.current(userId);
    }

    @Override
    public List<Player> past(List<User> user) {
        return dao.past(user);
    }

    @Override
    public List<Player> past(Integer userId) {
        return dao.past(userId);
    }

    @Override
    public List<Player> all(List<User> user) {
        return dao.all(user);
    }

    @Override
    public List<Player> all(Integer userId) {
        return dao.all(userId);
    }

    @Override
    public Player get(Integer id) {
        return dao.get(id);
    }
    
 
}
