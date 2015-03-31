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
    public List<UserChallengeGroup> getChallenges(Principal principal) {
        User u = userDao.get(principal.getName());
        List<UserChallengeGroup> challenges =  getPendingChallenges(u);
        challenges.addAll(getAcceptedChallenges(u));
        challenges.addAll(getChallenges(u, Status.CANCELLED));
        //challenges.sort((o1, o2) -> o1.getChallenges().getId().compareTo(o2.getChallenges().getId()));
        return challenges;
    }

    @RequestMapping(value = "/challenges/pending/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserChallengeGroup> getPending(@PathVariable Integer userId,Principal principal) {
        User u = userDao.get(userId);
        if (u == null) {
            return Collections.emptyList();
        }
        List<UserChallengeGroup> challenges = getSent(u);
        challenges.addAll(getNeedNotify(u));
        return challenges;
    }

    @RequestMapping(value = "/challenges/sent", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserChallengeGroup> getSent(Principal principal) {
        User u = userDao.get(principal.getName());
        return getSent(u);
    }

    @RequestMapping(value = "/challenges/pendingApproval", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserChallengeGroup> getPendingApproval(Principal principal) {
        User u = userDao.get(principal.getName());
        return getSent(u);
    }

    private List<UserChallengeGroup> getUserChallengeGroups(User u, Status status) {
        List<UserChallengeGroup> groupChallenges = new ArrayList<>();
        List<Challenge> challenges = new ArrayList<>();
        List<Player> userPlayers = playerDao.getByUser(u).stream().filter(p-> p.getDivision().isChallenge()).collect(Collectors.toList());

        for (Player userPlayer : userPlayers) {
            challenges.addAll(dao.getChallenges(u, status).stream().filter(c -> c.getChallenger().equals(userPlayer)).collect(Collectors.toList()));
        }
        List<UserChallengeGroup> groups = new ArrayList<>();

        for (Challenge challenge : challenges) {
            UserChallengeGroup group = new UserChallengeGroup();
            group.setChallenger(challenge.getChallenger().getUser());
            group.setOpponent(challenge.getOpponent().getUser());
            group.setDate(challenge.getSlot().getLocalDateTime().toLocalDate());
            if (!groups.contains(group)) {
                groups.add(group);
            }

            group = groups.get(groups.indexOf(group));
            group.addChallenge(challenge);
        }
        return groups;
    }

    private List<UserChallengeGroup> getSent(User u) {
        return getUserChallengeGroups(u,Status.PENDING);
    }

    private List<UserChallengeGroup> getNeedNotify(User u) {
        return getUserChallengeGroups(u,Status.NEEDS_NOTIFY);
    }

    private List<UserChallengeGroup> getPendingApproval(User u) {
        List<Player> userPlayers = playerDao.getByUser(u);
        List<UserChallengeGroup> challenges = getPendingChallenges(u);
        List<UserChallengeGroup> sent = new ArrayList<>();
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
    public UserChallengeGroup requestChallenge(@RequestBody ChallengeRequest request) {
        logger.info("Got request for challenge " + request);
        UserChallengeGroup userChallengeGroup = new UserChallengeGroup();
        User challenger = userDao.get(request.getChallenger().getId());
        User opponent = userDao.get(request.getOpponent().getId());

        List<Player> challengePlayers = playerDao.getByUser(challenger);
        List<Player> opponentPlayers = playerDao.getByUser(opponent);

        List<Challenge> challenges = new ArrayList<>();
        userChallengeGroup.setChallenger(challenger);
        userChallengeGroup.setOpponent(opponent);
        Player op = null;
        Player ch = null;
        if (request.isNine()) {
            op = opponentPlayers.stream().filter(p->p.getDivision().getType() == DivisionType.NINE_BALL_CHALLENGE).findFirst().orElseGet(null);
            ch = challengePlayers.stream().filter(p->p.getDivision().getType() == DivisionType.NINE_BALL_CHALLENGE).findFirst().orElseGet(null);
            challenges.addAll(createChallenge(request.getSlots(),ch,op));
        }

        if (request.isEight()) {
            op = opponentPlayers.stream().filter(p->p.getDivision().getType() == DivisionType.EIGHT_BALL_CHALLENGE).findFirst().orElseGet(null);
            ch = challengePlayers.stream().filter(p->p.getDivision().getType() == DivisionType.EIGHT_BALL_CHALLENGE).findFirst().orElseGet(null);
            challenges.addAll(createChallenge(request.getSlots(),ch,op));
        }

        userChallengeGroup.setChallenges(challenges);
        userChallengeGroup.setEight(request.isEight());
        userChallengeGroup.setNine(request.isNine());
        //TODO verify....
        userChallengeGroup.setDate(request.getSlots().get(0).getLocalDateTime().toLocalDate());
        return userChallengeGroup;
    }

    private List<Challenge> createChallenge(List<Slot> slots,Player ch, Player op) {
        if (ch == null || op == null) {
            logger.error("Got a challenge request with either no challenger of opponent: " + ch + "  "+  op);
            return Collections.emptyList();
        }

        List<Challenge> challenges = new ArrayList<>();
        for (Slot slot : slots) {
            Challenge c = new Challenge();
            c.setOpponent(op);
            c.setChallenger(ch);
            c.setSlot(slot);
            c.setStatus(Status.NEEDS_NOTIFY);
            //TODO JSON View
            Challenge response = new Challenge();
            c = dao.requestChallenge(c);

            response.setId(c.getId());
            response.setStatus(c.getStatus());
            response.setSlot(slotDao.get(c.getSlot().getId()));
            challenges.add(response);
        }
        return challenges;
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

    public List<UserChallengeGroup> getPendingChallenges(User u) {
        return getChallenges(u, Status.PENDING);
    }

    public List<UserChallengeGroup> getNeedNotifyChallenges(User u) {
        return getChallenges(u, Status.NEEDS_NOTIFY);
    }

    public List<UserChallengeGroup> getAcceptedChallenges(User u) {
        return getChallenges(u, Status.ACCEPTED);
    }

    private List<UserChallengeGroup> getChallenges(User u, Status status) {
        List<Challenge> challenges = dao.getChallenges(u,status);
        List<UserChallengeGroup> userChallengeGroups = new ArrayList<>(challenges.size());
        for (Challenge challenge : challenges) {
            userChallengeGroups.add(getGroupChallenge(challenge));
        }
        return userChallengeGroups;
    }

    private UserChallengeGroup getGroupChallenge(Challenge c) {
        UserChallengeGroup userChallengeGroup = new UserChallengeGroup();
        userChallengeGroup.setChallenger(c.getChallenger().getUser());
        userChallengeGroup.setOpponent(c.getOpponent().getUser());

        return userChallengeGroup;
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
