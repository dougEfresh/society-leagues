package com.society.leagues.resource.client;

import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.Division;
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
    public Collection<User> get() {
        return  dao.get().stream().sorted(new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getName().compareTo(o2.getName());
            }
        }).collect(Collectors.toList());
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

    @RequestMapping(value = "/userStats/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlayerStats> getStats(@PathVariable Integer userId) {
        User u = dao.get(userId);
        if (u == null) {
            return Collections.emptyList();
        }
        List<PlayerStats> playerStats = new ArrayList<>();
        playerDao.getByUser(u).forEach(p -> playerStats.add(getStats(p)));
        return playerStats;
    }

    @RequestMapping(value = "/allStats", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<Integer,UserStats>> getAllStats() {
        List<UserStats> stats = new ArrayList<>();
        for (User user : dao.get()) {
            ///UserStats s = getStats(user);
            //stats.add(s);
        }

        return Collections.emptyList();
    }

    public PlayerStats getStats(Player player) {

        List<PlayerResult> results = playerResultDao.get().stream().filter(p ->
                        p.getPlayerAway().equals(player) ||
                                p.getPlayerHome().equals(player)
        ).collect(Collectors.toList());

        PlayerStats playerStats = new PlayerStats();

        for (PlayerResult result : results) {
            if (result.getHomeRacks() > result.getAwayRacks()) {
                if (result.getPlayerHome().equals(player)){
                    playerStats.addWin(result.getHomeRacks());
                    playerStats.addPoints(3);
                } else {
                    playerStats.addPoints(1);
                    playerStats.addLost(result.getHomeRacks());
                }
            } else {
                if (result.getPlayerHome().equals(player)) {
                    playerStats.addPoints(1);
                    playerStats.addLost(result.getHomeRacks());
                } else {
                    playerStats.addPoints(3);
                    playerStats.addWin(result.getHomeRacks());
                }
            }
        }
        return playerStats;
    }

    public User get(String login) {
        return dao.get(login);
    }

}
