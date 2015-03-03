package com.society.leagues.resource.client;

import com.society.leagues.client.api.ChallengeApi;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.dao.ChallengeDao;
import com.society.leagues.dao.PlayerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RestController
//@RolesAllowed(value = {"ADMIN","PLAYER"})
public class ChallengeResource  implements ChallengeApi {

    @Autowired ChallengeDao dao;
    @Autowired PlayerDao playerDao;

    @RequestMapping(value = "/leaderBoard", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlayerStats> leaderBoard() {
        List<PlayerStats> stats = new ArrayList<>();
        Map<Integer,PlayerStats> userStats = new HashMap<>();
        List<Player> players = playerDao.get().stream().filter(p -> p.getDivision().isChallenge()).collect(Collectors.toList());

        for (Player player : players) {
            for (TeamMatch m : player.getTeamMatches().stream().filter(p -> p.getResult() != null).collect(Collectors.toList())) {
                if (!userStats.containsKey(player.getId())) {
                    userStats.put(player.getId(),new PlayerStats(player.getId()));
                }

                PlayerStats s = userStats.get(player.getId());
                
            }
        }

        for (Challenge challenge : dao.get()) {
            if (challenge.getTeamMatch().getResult() == null)
                continue;
            TeamMatch
        }
    }

    @Override
    public List<User> getPotentials(Integer id) {
        return dao.getPotentials(id);
    }

    @Override
    public Challenge requestChallenge(Challenge challenge) {
        return dao.requestChallenge(challenge);
    }

    @Override
    public Challenge acceptChallenge(Challenge challenge) {
        return dao.acceptChallenge(challenge);
    }

    @Override
    public List<Challenge> listChallenges(Integer userId) {
        return dao.listChallenges(userId);
    }

    @Override
    public Boolean cancelChallenge(Challenge challenge) {
        return dao.cancelChallenge(challenge);
    }

    @Override
    public Challenge modifyChallenge(Challenge challenge) {
        return dao.modifyChallenge(challenge);
    }

    @Override
    public List<Slot> slots(Date date) {
        return dao.slots(date);
    }

    @Override
    public List<Challenge> getByPlayer(Player p) {
        return null;
    }
}
