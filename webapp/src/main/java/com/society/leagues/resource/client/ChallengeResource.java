package com.society.leagues.resource.client;

import com.fasterxml.jackson.annotation.JsonView;
import com.society.leagues.EmailService;
import com.society.leagues.client.View;
import com.society.leagues.client.api.ChallengeApi;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.dao.ChallengeDao;
import com.society.leagues.dao.PlayerDao;
import com.society.leagues.dao.UserDao;
import com.society.leagues.util.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RestController
//@RolesAllowed(value = {"ADMIN","PLAYER"})
public class ChallengeResource  implements ChallengeApi {

    @Autowired EmailService emailService;
    @Autowired ChallengeDao dao;
    @Autowired PlayerDao playerDao;
    @Autowired UserDao userDao;

    @JsonView(value = View.PlayerId.class)
    @RequestMapping(value = "/challenges", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlayerChallenge> getPChallenges(Principal principal) {
        User u = userDao.get(principal.getName());
        List<PlayerChallenge> challenges =  getPendingChallenges(u);
        challenges.addAll(getAcceptedChallenges(u));
        return challenges;
    }

    public List<PlayerChallenge> getPendingChallenges(User u) {
      return getChallenges(u,Status.PENDING);
    }

    public List<PlayerChallenge> getAcceptedChallenges(User u) {
        return getChallenges(u,Status.ACCEPTED);
    }

    private List<PlayerChallenge> getChallenges(User u, Status status) {
        List<Challenge> challenges = new ArrayList<>();
        if (status == Status.PENDING)
            challenges.addAll(dao.getPending(u));
        else
            challenges.addAll(dao.getAccepted(u));

        List<PlayerChallenge> playerChallenges = new ArrayList<>(challenges.size());
        for (Challenge challenge : challenges) {
            PlayerChallenge playerChallenge = new PlayerChallenge();
            playerChallenge.setChallenge(challenge);
            //In challenge division, only one player per team
            playerChallenge.setChallenger(playerDao.findHomeTeamPlayers(challenge.getTeamMatch()).get(0));
            playerChallenge.setOpponent(playerDao.findAwayTeamPlayers(challenge.getTeamMatch()).get(0));
            playerChallenges.add(playerChallenge);
        }
        return playerChallenges;
    }


    @JsonView(value = View.PlayerId.class)
    @RequestMapping(value = "/potentials", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<User> getPotentials(Principal principal) {
        User u = userDao.get(principal.getName());
        return getPotentials(u);
    }

    public Collection<User> getPotentials(User u) {
        List<Player> players = dao.getPotentials(u.getId());
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

    @RequestMapping(value = "/leaderBoard", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlayerStats> leaderBoard() {
        List<PlayerStats> stats = new ArrayList<>();
        Map<Integer,PlayerStats> userStats = new HashMap<>();
        List<Player> players = playerDao.get().stream().filter(p -> p.getDivision().isChallenge()).collect(Collectors.toList());
        /*
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
        }
        */
        return null;
    }


    @Override
    public List<Player> getPotentials(Integer id) {
        return dao.getPotentials(id);
    }

    public Challenge requestChallenge(List<Player> players) {
        if (players == null || players.size() != 2)
            return null;

        return  null ;//requestChallenge(players.get(0),players.get(1));
    }

    public Challenge requestChallenge(ChallengeRequest challenge) {

        return  null ; //dao.requestChallenge(challenge);
    }


    @RequestMapping(value = "/challenge/accept/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Challenge acceptChallenge(@PathVariable (value = "id") Integer id) {
        Challenge c  = new Challenge();
        c.setId(id);
        return dao.acceptChallenge(c);
    }

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

    private Challenge modifyChallenge(User user, Challenge challenge) {
        Email email = new Email();
        return modifyChallenge(challenge);
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
