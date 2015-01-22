package com.society.leagues.resource;

import com.society.leagues.client.api.UserApi;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@SuppressWarnings("unused")
public class UserResource extends ApiResource implements UserApi {
    @Autowired UserDao dao;

    @Override
    public List<User> current(List<User> users) {
        return dao.current(users);
    }

    @Override
    public List<User> current(Integer userId) {
        return dao.current(userId);
    }

    @Override
    public List<User> past(List<User> user) {
        return dao.past(user);
    }

    @Override
    public List<User> past(Integer userId) {
        return dao.past(userId);
    }

    @Override
    public List<User> all(List<User> user) {
        return dao.all(user);
    }

    @Override
    public List<User> all(Integer userId) {
        return dao.all(userId);
    }

    @Override
    public List<User> all() {
        return dao.all();
    }

    @Override
    public User get(Integer id) {
        return dao.get(id);
    }
}
