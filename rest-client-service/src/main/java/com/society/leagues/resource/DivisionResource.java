package com.society.leagues.resource;

import com.society.leagues.client.api.DivisionClientApi;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.dao.DivisionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@SuppressWarnings("unused")
public class DivisionResource extends ApiResource implements DivisionClientApi {

    @Autowired DivisionDao dao;

    @Override
    public List<Division> current(List<User> users) {
        return dao.current(users);
    }

    @Override
    public List<Division> current(Integer userId) {
        return dao.current(userId);
    }

    @Override
    public List<Division> past(List<User> user) {
        return dao.past(user);
    }

    @Override
    public List<Division> past(Integer userId) {
        return dao.past(userId);
    }

    @Override
    public List<Division> all(List<User> user) {
        return dao.all(user);
    }

    @Override
    public List<Division> all(Integer userId) {
        return dao.all(userId);
    }

    @Override
    public Division get(Integer id) {
        return dao.get(id);
    }

    @Override
    public List<Division> get() {
        return dao.get();
    }
}
