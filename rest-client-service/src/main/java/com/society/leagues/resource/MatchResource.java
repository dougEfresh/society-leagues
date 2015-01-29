package com.society.leagues.resource;

import com.society.leagues.client.api.MatchApi;
import com.society.leagues.client.api.domain.Match;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.dao.MatchDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@SuppressWarnings("unused")
public class MatchResource extends ApiResource implements MatchApi {
    @Autowired MatchDao dao;

    @Override
    public Match get(Integer id) {
        return dao.get(id);
    }

    @Override
    public List<Match> current(List<User> users) {
        return null;
    }

    @Override
    public List<Match> current(Integer userId) {
        return null;
    }

    @Override
    public List<Match> past(List<User> user) {
        return null;
    }

    @Override
    public List<Match> past(Integer userId) {
        return null;
    }

    @Override
    public List<Match> all(List<User> user) {
        return null;
    }

    @Override
    public List<Match> all(Integer userId) {
        return null;
    }

    @Override
    public List<Match> get() {
        return dao.get();
    }
}

