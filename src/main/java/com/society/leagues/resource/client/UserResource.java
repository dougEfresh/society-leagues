package com.society.leagues.resource.client;

import com.society.leagues.adapters.UserAdapter;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
@SuppressWarnings("unused")
public class UserResource  {
    @Autowired UserDao dao;
    @Autowired PlayerDao playerDao;
    @Autowired PlayerResultDao playerResultDao;
    @Autowired ChallengeDao challengeDao;
    @Autowired TeamResultDao teamResultDao;
    private static Logger logger = LoggerFactory.getLogger(UserResource.class);

    @RequestMapping(value = "/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public User get(Principal principal) {
        return  get(principal.getName());
    }


    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public User get(@PathVariable Integer id) {
        if ( id == null || id == 0 ) {
            User u = new User();
            u.setFirstName("");
            u.setLastName("");
            u.setId(0);
            return u;
        }
        return dao.get(id);
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<UserAdapter> get() {
        List<User> users = dao.get().stream().sorted(new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getName().compareTo(o2.getName());
            }
        }).filter(u -> !(
                        u.getName().contains("FORFEIT") || u.getName().contains("BYE")  || u.getName().contains("HANDICAP")
                )
        ).collect(Collectors.toList());

        List<UserAdapter> userAdapters = new ArrayList<>();
        for (User user : users) {
            UserAdapter adapter = new UserAdapter(user,playerDao.getByUser(user));
            userAdapters.add(adapter);
        }
        return userAdapters;
    }

    @RequestMapping(value = "/results", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<PlayerResult> getResults(Principal principal) {
        return playerResultDao.get();
    }

    public User get(String login) {
        return dao.get(login);
    }

}
