package com.society.leagues.resource;

import com.society.leagues.client.api.UserClientApi;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@SuppressWarnings("unused")
public class UserResource extends ApiResource implements UserClientApi {
    @Autowired UserDao dao;

    @Override
    public User get(Integer id) {
        return dao.get(id);
    }

    @Override
    public List<User> get() {
        return dao.get();
    }

    @Override
    public User get(String login) {
        return dao.get(login);
    }

    @Override
    public List<User> get(List<Integer> id) {
        return dao.get(id);
    }
}
