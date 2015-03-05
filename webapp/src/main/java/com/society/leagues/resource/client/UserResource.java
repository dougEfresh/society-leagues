package com.society.leagues.resource.client;

import com.society.leagues.client.api.UserClientApi;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.dao.*;
import com.society.leagues.resource.ApiResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.security.Principal;

@RestController
@SuppressWarnings("unused")
public class UserResource extends ApiResource implements UserClientApi {
    @Autowired UserDao dao;
    @Autowired PlayerDao playerDao;
    @Autowired PlayerResultDao playerResultDao;
    @Autowired ChallengeDao challengeDao;
    @Autowired TeamResultDao teamResultDao;
    private static Logger logger = LoggerFactory.getLogger(UserResource.class);

    @RequestMapping(value = "/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public User get(Principal principal) {
        User u = get(principal.getName());

        return u;
    }

    @RequestMapping(value = "/players", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Player> getPlayers(Principal principal) {
        User u = get(principal.getName());
        List<Player> players = playerDao.getByUser(u);
        return players;
    }

    @RequestMapping(value = "/challenges", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Challenge> getChallenges(Principal principal) {
        User u = get(principal.getName());
        List<Challenge> challenges = new ArrayList<>();
        challenges.addAll(challengeDao.getAccepted(u));
        challenges.addAll(challengeDao.getPending(u));
        return challenges;
    }

    @RequestMapping(value = "/results", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlayerResult> getResults(Principal principal) {
        return playerResultDao.get();
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
