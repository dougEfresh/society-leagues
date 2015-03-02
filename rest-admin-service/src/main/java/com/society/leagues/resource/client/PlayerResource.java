package com.society.leagues.resource.client;

import com.society.leagues.client.api.PlayerClientApi;
import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.dao.PlayerDao;

import com.society.leagues.resource.ApiResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;


@Component
@SuppressWarnings("unused")
public class PlayerResource extends ApiResource implements PlayerClientApi {
    @Autowired PlayerDao dao;

    @Override
    public Player get(Integer id) {
        return dao.get(id);
    }

    @Override
    public List<Player> get() {
        return dao.get();
    }

    @Override
    public List<Player> get(List<Integer> id) {
        return dao.get(id);
    }

}
