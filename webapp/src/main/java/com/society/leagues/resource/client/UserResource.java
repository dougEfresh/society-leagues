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
import java.util.stream.Collectors;

@RestController
@SuppressWarnings("unused")
public class UserResource extends ApiResource  {
    @Autowired UserDao dao;
    @Autowired PlayerDao playerDao;
    @Autowired PlayerResultDao playerResultDao;
    @Autowired ChallengeDao challengeDao;
    @Autowired TeamResultDao teamResultDao;   private static Logger logger = LoggerFactory.getLogger(UserResource.class);

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
    @RequestMapping(value = "/results", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlayerResult> getResults(Principal principal) {
        return playerResultDao.get();
    }

    @RequestMapping(value = "/userStats", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserStats> getStats(Principal principal) {
        User u = get(principal.getName());
        UserStats stats = getStats(u);
        stats.setUser(u);
        return Arrays.asList(stats);
    }
    @RequestMapping(value = "/allStats", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserStats> getAllStats() {
        List<UserStats> stats = new ArrayList<>();
        for (User user : dao.get()) {
            UserStats s = getStats(user);
            stats.add(s);
        }

        return stats;
    }

    public UserStats getStats(User user) {

        List<PlayerResult> results = playerResultDao.get().stream().filter(p ->
                        p.getPlayerAway().getUser().equals(user) ||
                                p.getPlayerHome().getUser().equals(user)
        ).collect(Collectors.toList());

        UserStats userStats = new UserStats();
        for (PlayerResult result : results) {
            if (result.getHomeRacks() > result.getAwayRacks()) {
                if (result.getPlayerHome().getUser().equals(user)) {
                    userStats.addWin(result.getHomeRacks());
                } else {
                    userStats.addLost(result.getHomeRacks());
                }
            } else {
                if (result.getPlayerHome().getUser().equals(user)) {
                    userStats.addLost(result.getHomeRacks());
                } else {
                    userStats.addWin(result.getHomeRacks());
                }
            }
        }
        userStats.setUser(user);
        return userStats;
    }

    public User get(String login) {
        return dao.get(login);
    }

}
