package com.society.leagues.resource.client;

import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.DivisionType;
import com.society.leagues.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RestController
public class ChallengeResource  {
    private static Logger logger = LoggerFactory.getLogger(ChallengeResource.class);
    @Autowired ChallengeDao dao;
    @Autowired PlayerDao playerDao;
    @Autowired PlayerResultDao playerResultDao;
    @Autowired UserDao userDao;
    @Autowired SlotDao slotDao;

    @RequestMapping(value = "/challenges", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlayerChallenge> getChallenges(Principal principal) {
        User u = userDao.get(principal.getName());
        List<PlayerChallenge> challenges =  getPendingChallenges(u);
        challenges.addAll(getAcceptedChallenges(u));
        challenges.addAll(getChallenges(u, Status.CANCELLED));
        //challenges.sort((o1, o2) -> o1.getChallenges().getId().compareTo(o2.getChallenges().getId()));
        return challenges;
    }

    @RequestMapping(value = "/challenges/pending", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlayerChallenge> getPending(Principal principal) {
        User u = userDao.get(principal.getName());
        return getSent(u);
    }

    @RequestMapping(value = "/challenges/sent", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlayerChallenge> getSent(Principal principal) {
        User u = userDao.get(principal.getName());
        return getSent(u);
    }

    @RequestMapping(value = "/challenges/pendingApproval", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlayerChallenge> getPendingApproval(Principal principal) {
        User u = userDao.get(principal.getName());
        return getSent(u);
    }

    private List<PlayerChallenge> getSent(User u) {
        List<Player> userPlayers = playerDao.getByUser(u);
        List<PlayerChallenge> challenges = getPendingChallenges(u);
        List<PlayerChallenge> sent = new ArrayList<>();
        sent.addAll(challenges.stream().filter( p->p.getChallenger().equals(userPlayers.get(0))).collect(Collectors.toList()));
        sent.addAll(challenges.stream().filter( p->p.getChallenger().equals(userPlayers.get(1))).collect(Collectors.toList()));
        return sent;
    }

    private List<PlayerChallenge> getPendingApproval(User u) {
        List<Player> userPlayers = playerDao.getByUser(u);
        List<PlayerChallenge> challenges = getPendingChallenges(u);
        List<PlayerChallenge> sent = new ArrayList<>();
        sent.addAll(challenges.stream().filter( p->p.getOpponent().equals(userPlayers.get(0))).collect(Collectors.toList()));
        sent.addAll(challenges.stream().filter( p->p.getOpponent().equals(userPlayers.get(1))).collect(Collectors.toList()));
        return sent;
    }


    @RequestMapping(value = "/challenge/potentials", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<UserChallenge> getPotentials(Principal principal) {
        User u = userDao.get(principal.getName());
	return getPotentials(u.getId());
    }


    @RequestMapping(value = "/challenge/potentials/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<UserChallenge> getPotentials(@PathVariable Integer  userId) {
        User u = userDao.get(userId);
        Collection<UserChallenge> potentials = getPotentials(u);
        return potentials.stream().sorted(new Comparator<UserChallenge>() {
            @Override
            public int compare(UserChallenge o1, UserChallenge o2) {
                return o1.getUser().getName().compareTo(o2.getUser().getName());
            }
        }).collect(Collectors.toList());
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

        for (Slot slot : request.getSlotTimes()) {
            Challenge c = new Challenge();
            c.setOpponent(opponent);
            c.setChallenger(challenger);
            c.setSlot(slot);
            c.setStatus(Status.PENDING);
            //TODO Move to JSON View
            Challenge response = new Challenge();
            c = dao.requestChallenge(c);
            response.setId(c.getId());
            response.setStatus(c.getStatus());
            response.setSlot(slot);
            challenges.add(response);
        }
        playerChallenge.setChallenges(challenges);
        return playerChallenge;
    }

    @RequestMapping(value = "/challenge/slots/{date}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public List<Slot> getSlots(@PathVariable(value = "date") String date) throws ParseException {
            return slotDao.get(LocalDate.parse(date).atStartOfDay());
    }

    @RequestMapping(value = "/challenge/counters", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Integer> getCounters(Principal principal) {
        User u = userDao.get(principal.getName());
        return getCounters(u.getId());
    }

    @RequestMapping(value = "/challenge/counters/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Integer> getCounters(@PathVariable Integer userId) {
        User u = userDao.get(userId);
        List<Integer> counters = new ArrayList<>();
        counters.add(getSent(u).size());
        counters.add(getPendingApproval(u).size());
        return counters;
    }

    public List<PlayerChallenge> getPendingChallenges(User u) {
        return getChallenges(u,Status.PENDING);
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
        Collection<Player> players = dao.getPotentials(u.getId());
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
            switch (player.getDivision().getType()) {
                case EIGHT_BALL_CHALLENGE:
                    p.setDivision(player.getDivision());
                    userChallenge.setEightBallPlayer(p);
                    break;
                case NINE_BALL_CHALLENGE:
                    p.setDivision(player.getDivision());
                    userChallenge.setNineBallPlayer(p);
                    break;
            }
        }
        return users.values();
    }

}
