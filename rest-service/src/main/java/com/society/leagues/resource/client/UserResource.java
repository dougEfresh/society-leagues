package com.society.leagues.resource.client;

import com.society.leagues.client.api.UserClientApi;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.dao.UserDao;
import com.society.leagues.resource.ApiResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.security.Principal;

@RestController
@SuppressWarnings("unused")
public class UserResource extends ApiResource implements UserClientApi {
    @Autowired UserDao dao;

    @RequestMapping(value = "/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public User get(Principal principal) {
        User u = get(principal.getName());

        return u;
    }

    @Override
    public User get(@PathVariable(value = "id") Integer id) {
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
    public List<User> get(@RequestBody List<Integer> id) {
        return dao.get(id);
    }
}
