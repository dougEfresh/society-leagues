package com.society.leagues.resource.client;

import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.PlayerResult;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.client.api.domain.UserStats;
import com.society.leagues.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
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
    public Collection<Player> getPlayers() {
        return playerDao.get();
    }

    @RequestMapping(value = "/results", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<PlayerResult> getResults(Principal principal) {
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
                    userStats.addPoints(3);
                } else {
                    userStats.addPoints(1);
                    userStats.addLost(result.getHomeRacks());
                }
            } else {
                if (result.getPlayerHome().getUser().equals(user)) {
                    userStats.addPoints(1);
                    userStats.addLost(result.getHomeRacks());
                } else {
                    userStats.addPoints(3);
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
