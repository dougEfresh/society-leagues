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
    @Autowired ChallengeResource challengeResource;

    private static Logger logger = LoggerFactory.getLogger(UserResource.class);

    @RequestMapping(value = "/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserAdapter get(Principal principal) {
	if (principal == null) {
	    return UserAdapter.DEFAULT_USER;
	}
        return  get(principal.getName());
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserAdapter get(@PathVariable Integer id) {
        return get().get(id);
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserAdapter> get() {
        List<User> users = dao.get().stream().sorted(new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getName().compareTo(o2.getName());
            }
        }).filter(u -> !(
                        u.getName().contains("FORFEIT") || u.getName().contains("BYE")  || u.getName().contains("HANDICAP")
                )
        ).collect(Collectors.toList());
        return users.stream().map(user -> new UserAdapter(user, playerDao.getByUser(user), challengeResource.getChallenges(user.getId()))).collect(Collectors.toList());
    }

    public UserAdapter get(String login) {
        User u = dao.get(login);
	return  new UserAdapter(u,playerDao.getByUser(u),challengeResource.getChallenges(u.getId()));
    }

}
