package com.society.leagues.resource.client;

import com.fasterxml.jackson.annotation.JsonView;
import com.society.leagues.client.View;
import com.society.leagues.client.api.UserClientApi;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.dao.*;
import com.society.leagues.resource.ApiResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import java.security.Principal;

@RestController
@SuppressWarnings("unused")
public class UserResource extends ApiResource  {
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

    @RequestMapping(value = "/userPlayers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Player> getUserPlayers(Principal principal) {
        User u = get(principal.getName());
        return getUserPlayers(u);
    }

    public List<Player> getUserPlayers(User u) {
        List<Player> players = playerDao.getByUser(u);
        return players;
    }

    @RequestMapping(value = "/players", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Player> getPlayers() {
        return playerDao.get();
    }

    @JsonView(value = View.PlayerId.class)
    @RequestMapping(value = "/challenges", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Challenge> getChallenges(Principal principal) {
        User u = get(principal.getName());
        return getChallenges(u);
    }

    public List<Challenge> getChallenges(User u) {
        List<Challenge> challenges = new ArrayList<>();
        challenges.addAll(challengeDao.getAccepted(u));
        challenges.addAll(challengeDao.getPending(u));
        return challenges;
    }


    @JsonView(value = View.PlayerId.class)
    @RequestMapping(value = "/results", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlayerResult> getResults(Principal principal) {
        return playerResultDao.get();
    }

    @JsonView(value = View.PlayerId.class)
    @RequestMapping(value = "/potentials", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<User> getPotentials(Principal principal) {
        User u = get(principal.getName());
        return getPotentials(u);
    }

    public Collection<User> getPotentials(User u) {
        List<Player> players = challengeDao.getPotentials(u.getId());
        HashMap<Integer,User> users  = new HashMap<>();
        for (Player player : players) {
            if (!users.containsKey(player.getUser().getId())) {
                users.put(player.getUser().getId(), player.getUser());
            }
            User user = users.get(player.getUserId());
            user.addPlayer(player);
        }
        return users.values();
    }


    public User get(String login) {
        return dao.get(login);
    }

}
