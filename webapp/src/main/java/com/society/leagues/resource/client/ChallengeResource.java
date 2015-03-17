package com.society.leagues.resource.client;

import com.fasterxml.jackson.annotation.JsonView;
import com.society.leagues.EmailService;
import com.society.leagues.client.View;
import com.society.leagues.client.api.ChallengeApi;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.DivisionType;
import com.society.leagues.dao.*;
import com.society.leagues.util.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RestController
//@RolesAllowed(value = {"ADMIN","PLAYER"})
public class ChallengeResource  implements ChallengeApi {
    private static Logger logger = LoggerFactory.getLogger(ChallengeResource.class);
    @Autowired EmailService emailService;
    @Autowired ChallengeDao dao;
    @Autowired PlayerDao playerDao;
    @Autowired PlayerResultDao playerResultDao;
    @Autowired UserDao userDao;
    @Autowired SlotDao slotDao;

    @JsonView(value = View.PlayerId.class)
    @RequestMapping(value = "/challenges", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlayerChallenge> getPChallenges(Principal principal) {
        User u = userDao.get(principal.getName());
        List<PlayerChallenge> challenges =  getPendingChallenges(u);
        challenges.addAll(getAcceptedChallenges(u));
        challenges.addAll(getChallenges(u, Status.CANCELLED));
        //challenges.sort((o1, o2) -> o1.getChallenges().getId().compareTo(o2.getChallenges().getId()));
        return challenges;
    }

    @JsonView(value = View.PlayerId.class)
    @RequestMapping(value = "/challenges/pending", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlayerChallenge> getPending(Principal principal) {
        User u = userDao.get(principal.getName());
        List<PlayerChallenge> challenges =  getPendingChallenges(u);
        return challenges;
    }

    @RequestMapping(value = "/challenge/potentials", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<UserChallenge> getPotentials(Principal principal) {
        User u = userDao.get(principal.getName());
        return  getPotentials(u);
    }

    @RequestMapping(value = "/challenge/leaderBoard", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlayerStats> leaderBoard() {
        List<PlayerStats> stats = new ArrayList<>();
        Map<Integer,PlayerStats> userStats = new HashMap<>();
        List<Player> players = playerDao.get().stream().filter(p -> p.getDivision().isChallenge()).collect(Collectors.toList());

        for (Player player : players) {
            PlayerStats s = new PlayerStats();
            s.setUserId(player.getUserId());
            userStats.put(player.getUserId(),s);
        }

        return null;
    }

    @RequestMapping(value = "/challenge/cancel/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Challenge cancelChallenge(@PathVariable (value = "id") Integer id) {
        Challenge c = dao.get(id);
        return dao.cancelChallenge(c);
    }

    @RequestMapping(value = "/challenge/accept/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Challenge acceptChallenge(@PathVariable (value = "id") Integer id) {
        Challenge c = dao.get(id);
        c = dao.acceptChallenge(c);
        return c;
    }

    @RequestMapping(value = "/challenge/request",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public PlayerChallenge requestChallenge(@RequestBody ChallengeRequest request) {
        logger.info("Got request for challenge " + request);
        PlayerChallenge playerChallenge = new PlayerChallenge();
        Player challenger = playerDao.get(request.getChallenger().getId());
        Player opponent = playerDao.get(request.getOpponent().getId());

        List<Challenge> challenges = new ArrayList<>();
        playerChallenge.setChallenger(challenger);
        playerChallenge.setOpponent(opponent);

        for (LocalDateTime localDateTime : request.getChallengeTimes()) {
            Challenge c = new Challenge();
            c.setOpponent(opponent);
            c.setChallenger(challenger);
            c.setChallengeTime(localDateTime);
            c.setStatus(Status.PENDING);
            //TODO Move to JSON View
            Challenge response = new Challenge();
            c = dao.requestChallenge(c);
            response.setId(c.getId());
            response.setStatus(c.getStatus());
            response.setChallengeTime(c.getChallengeTime());
            challenges.add(response);
        }
        playerChallenge.setChallenges(challenges);
        return playerChallenge;
    }

    @RequestMapping(value = "/challenge/slots/{date}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<Slot> getSlots(@PathVariable(value = "date") String date) throws ParseException {
            return slotDao.get(LocalDate.parse(date).atStartOfDay());
    }

    public List<PlayerChallenge> getPendingChallenges(User u) {
        return null;
    }

    public List<PlayerChallenge> getAcceptedChallenges(User u) {
        return getChallenges(u, Status.ACCEPTED);
    }

    private List<PlayerChallenge> getChallenges(User u, Status status) {
        List<Challenge> challenges = dao.getChallenges(u,status);
        List<PlayerChallenge> playerChallenges = new ArrayList<>(challenges.size());
        for (Challenge challenge : challenges) {
            playerChallenges.add(getPlayerChallenge(challenge));
        }
        return playerChallenges;
    }

    private PlayerChallenge getPlayerChallenge(Challenge c) {
        PlayerChallenge playerChallenge = new PlayerChallenge();
        playerChallenge.setChallenger(c.getChallenger());
        playerChallenge.setOpponent(c.getOpponent());
        return playerChallenge;
    }

    public Collection<UserChallenge> getPotentials(User u) {
        List<Player> players = dao.getPotentials(u.getId());
        HashMap<Integer,UserChallenge> users  = new HashMap<>();
        for (Player player : players) {
            if (!users.containsKey(player.getUser().getId())) {
                UserChallenge userChallenge = new UserChallenge();
                userChallenge.setUser(player.getUser());
                users.put(player.getUser().getId(), userChallenge);
            }

            Player p = new Player();
            p.setId(player.getId());
            p.setHandicap(player.getHandicap());

            UserChallenge userChallenge = users.get(player.getUserId());
            if (player.getDivision().getType() == DivisionType.EIGHT_BALL_CHALLENGE) {
                p.setDivision(player.getDivision());

                userChallenge.setEightBallPlayer(p);
            } else {
                p.setDivision(player.getDivision());
                userChallenge.setNineBallPlayer(p);
            }
        }
        return users.values();
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

    private Challenge modifyChallenge(User user, Challenge challenge) {
        Email email = new Email();
        return modifyChallenge(challenge);
    }

    @Override
    public Challenge modifyChallenge(Challenge challenge) {
            return dao.modifyChallenge(challenge);
    }


    @Override
    public List<Challenge> getByPlayer(Player p) {
        return null;
    }
}
