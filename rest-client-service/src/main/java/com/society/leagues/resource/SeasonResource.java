package com.society.leagues.resource;

import com.society.leagues.client.api.SeasonClientApi;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.dao.SeasonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@SuppressWarnings("unused")
public class SeasonResource extends ApiResource implements SeasonClientApi {
    @Autowired SeasonDao dao;

    @Override
    public List<Season> current(List<User> users) {
        return  dao.current(users);
    }

    @Override
    public List<Season> current(Integer userId) {
        return dao.current(userId);
    }

    @Override
    public List<Season> past(List<User> user) {
        return dao.past(user);
    }

    @Override
    public List<Season> past(Integer userId) {
        return dao.past(userId);
    }

    @Override
    public List<Season> all(List<User> user) {
        return dao.all(user);
    }

    @Override
    public List<Season> all(Integer userId) {
        return dao.all(userId);
    }

    @Override
    public Season get(Integer id) {
        return dao.get(id);
    }

    @Override
    public List<Season> get() {
        return dao.get();
    }
}
