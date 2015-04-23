package com.society.leagues.resource.client;

import com.society.leagues.EmailSender;
import com.society.leagues.EmailTaskRunner;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.DivisionType;
import com.society.leagues.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RequestMapping(value = "/api")
@RestController
public class ChallengeResource  {
    private static Logger logger = LoggerFactory.getLogger(ChallengeResource.class);
    @Autowired ChallengeDao dao;
    @Autowired PlayerDao playerDao;
    @Autowired PlayerResultDao playerResultDao;
    @Autowired UserDao userDao;
    @Autowired SlotDao slotDao;
    @Autowired EmailSender emailSender;
    @Value("${service-url:http://leaguesdev.societybilliards.com}") String serviceUrl;
    ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(400);

    @RequestMapping(value = "/challenge/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Status,List<UserChallengeGroup>> getChallenges(@PathVariable Integer userId) {
        User u = userDao.get(userId);
        Map<Status,List<UserChallengeGroup>> challenges = new HashMap<>();
        for (Status status : Status.values()) {
            challenges.put(status,getUserChallengeGroups(u, status));
        }
        challenges.put(Status.SENT,
                challenges.get(Status.PENDING).stream().
                        filter(c -> c.getChallenger().equals(u)).
                        collect(Collectors.toList())
        );
        challenges.put(Status.PENDING,
                challenges.get(Status.PENDING).stream().
                        filter(c -> !c.getChallenger().equals(u)).
                        collect(Collectors.toList())
        );
        challenges.put(Status.NOTIFY,
                challenges.get(Status.NOTIFY).stream().
                        filter(c -> c.getChallenger().equals(u)).
                        collect(Collectors.toList())
        );
        challenges.put(Status.ACCEPTED,
                challenges.get(Status.ACCEPTED).stream().
                        filter(c -> !c.getDate().isBefore(LocalDate.now())).
                        collect(Collectors.toList())
        );
        for (Status status : challenges.keySet()) {
            for(UserChallengeGroup group : challenges.get(status)) {
                group.setStatus(status);
            }
        }
        return challenges;
    }

    private List<UserChallengeGroup> getUserChallengeGroups(User u, Status status) {
        Collection<Challenge> challenges = dao.get().stream().
                filter(c -> c.getOpponent().getUser().equals(u) ||
                                c.getChallenger().getUser().equals(u)
                ).filter(c -> c.getStatus() == status).collect(Collectors.toList());

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
        return getUserChallengeGroups(u,Status.NOTIFY);
    }

    private List<UserChallengeGroup> getPendingApproval(User u) {
        List<Player> userPlayers = playerDao.getByUser(u);
        List<UserChallengeGroup> challenges = getPendingChallenges(u);
        List<UserChallengeGroup> sent = new ArrayList<>();
        sent.addAll(challenges.stream().filter( p->p.getOpponent().equals(userPlayers.get(0))).collect(Collectors.toList()));
        sent.addAll(challenges.stream().filter( p->p.getOpponent().equals(userPlayers.get(1))).collect(Collectors.toList()));
        return sent;
    }


    @RequestMapping(value = "/challenge/potentials/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserChallenge> getPotentials(@PathVariable Integer userId) {
        User u = userDao.get(userId);
        List<UserChallenge> potentials = new ArrayList<>();
        userDao.get().stream().filter(user -> !user.equals(u)).forEach(user -> potentials.add(new UserChallenge(user)));
        potentials.stream().forEach(
                user -> user.setNineBallPlayer(playerDao.getByUser(user.getUser()).stream().filter(p -> p.getDivision().getType() == DivisionType.NINE_BALL_CHALLENGE).findFirst().orElse(null))
        );

        potentials.stream().forEach(
                user -> user.setEightBallPlayer(playerDao.getByUser(user.getUser()).stream().filter(p->p.getDivision().getType() == DivisionType.EIGHT_BALL_CHALLENGE).findFirst().orElse(null))
        );
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
            //s.setUserId(player.getUserId());
            userStats.put(player.getUserId(),s);
        }

        return null;
    }

    @RequestMapping(value = "/challenge/status/{userId}/{status}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
	    public  Map<Status,List<UserChallengeGroup>> changeStatus(@PathVariable Integer userId, @PathVariable String status, @RequestBody UserChallengeGroup challengeGroup) {
        if ( challengeGroup.getChallenges() == null || challengeGroup.getChallenges().isEmpty()) {
            return getChallenges(userId);
        }
        if (Status.valueOf(status) == Status.CANCELLED)
            challengeGroup.getChallenges().forEach(dao::cancelChallenge);

        if (Status.valueOf(status) == Status.PENDING) {
            for (Challenge challenge : challengeGroup.getChallenges()) {
                Challenge c = dao.get(challenge.getId());
                c.setStatus(Status.PENDING);
                dao.modifyChallenge(c);
            }
        }

        if (Status.valueOf(status) == Status.ACCEPTED) {
            challengeGroup.getChallenges().forEach(dao::acceptChallenge);
        }
        return getChallenges(userId);
    }

    @RequestMapping(value = {"/challenge/cancel/{userId}","/challenge/cancelled/{userId}"},
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
  public Map<Status,List<UserChallengeGroup>> cancel(@PathVariable Integer userId, @RequestBody UserChallengeGroup challengeGroup) {
        User user = userDao.get(userId);
        Challenge c = challengeGroup.getChallenges().get(0);
        User opponent =userDao.get(dao.get(c.getId()).getOpponent().getUserId());
        dao.cancel(challengeGroup.getChallenges());
        sendEmail(user,opponent,Status.CANCELLED);
      return getChallenges(userId);
    }

    @RequestMapping(value = "/challenge/notify/{userId}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
  public Map<Status,List<UserChallengeGroup>> notify(@PathVariable Integer userId, @RequestBody UserChallengeGroup challengeGroup) {
        User user = userDao.get(userId);
        Challenge c = challengeGroup.getChallenges().get(0);
        User opponent =userDao.get(dao.get(c.getId()).getOpponent().getUserId());
        dao.pending(challengeGroup.getChallenges());
        sendEmail(user,opponent,Status.NOTIFY);
        return getChallenges(userId);
    }

    @RequestMapping(value = {"/challenge/accept/{userId}", "/challenge/accepted/{userId}"},
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<Status,List<UserChallengeGroup>> accept(@PathVariable Integer userId, @RequestBody Challenge challenge) {
        Map<Status,List<UserChallengeGroup>> challengeGroups = getChallenges(userId);
        User user = userDao.get(userId);
        final Challenge c  = dao.get(challenge.getId());
        if (user == null || c == null) {
            return Collections.emptyMap();
        }
        User opponent = userDao.get(c.getChallenger().getUserId());
        List<Challenge> toCancel = dao.get().stream().
                filter(ch->ch.getSlot().getLocalDateTime().toLocalDate().isEqual(c.getSlot().getLocalDateTime().toLocalDate())).
                filter(ch->ch.getChallenger().getUser().equals(c.getChallenger().getUser()) || ch.getOpponent().getUser().equals(c.getOpponent().getUser())).
                collect(Collectors.toList());
        for (Challenge ch : toCancel) {
            logger.info("Cancel: " + ch.getSlot().getLocalDateTime().toLocalDate() + " " +  ch.getId()  + " " + ch.getChallenger().getDivision().getType());
            ch.setStatus(Status.CANCELLED);
        }
        dao.cancel(toCancel);
        dao.acceptChallenge(c);
        sendEmail(user,opponent,Status.ACCEPTED);
        return getChallenges(userId);
    }

    private void sendEmail(User to, User from, Status status) {
        String subject;
        String body;
        Thread thread;
        EmailTaskRunner taskRunner;

        switch (status) {
            case NOTIFY:
                subject = "Society Leagues - Challenge Request From " + from.getName();
                body =  String.format("Hello %s,\nYou have a challenge request from %s.\nClick %s for details and approve!\n" +
                                "Thanks,    From the wonderful people of Society\n",
                        to.getName(), from.getName(), serviceUrl);
                break;
            case ACCEPTED:
                subject = "Society Leagues - Challenge Request Accepted - " + from.getName();
                body = String.format("Hello %s,\nYour challenge from %s has been accepted.\nSee %s for details.\n" +
                                "Thanks,    From the wonderful people of Society\n",
                                to.getName(), from.getName(), serviceUrl);

                break;
            case CANCELLED:
                subject = "Society Leagues - Challenge Request Declined - " + from.getName();
                body = String.format("Hello %s,\n%s has declined the challenge.\nClick %s for another challenge.\n" +
                                "Thanks,    From the wonderful people of Society\n",
                                to.getName(), from.getName(), serviceUrl);

                break;
            default:
                logger.warn("Got email request for unknown status " + status);
                return;

        }

        taskRunner = new EmailTaskRunner(emailSender,subject,body,to);
        Double ran = new Double(Math.random() * 10);
        Integer wait = new Double(Math.ceil(ran)).intValue();
        logger.info("Adding email to " + to.getName() + " to the thread pool. Delay: " + wait);
        threadPoolExecutor.schedule(taskRunner,wait, TimeUnit.MINUTES);
    }

    @RequestMapping(value = "/challenge/request",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<Status,List<UserChallengeGroup>>  requestChallenge(@RequestBody ChallengeRequest request) {
        logger.info("Got request for challenge " + request);
        User u = userDao.get(request.getChallenger().getId());
        User opponent = userDao.get(request.getOpponent().getId());

        for (Slot slot : request.getSlots()) {
            if (request.isEight())
                createChallenge(u,opponent,DivisionType.EIGHT_BALL_CHALLENGE,slot);

            if (request.isNine())
                createChallenge(u,opponent,DivisionType.NINE_BALL_CHALLENGE,slot);
        }
        return getChallenges(u.getId());
    }

    private void createChallenge(User challenger, User opponent, DivisionType type, Slot slot) {
        Challenge challenge = new Challenge();
        challenge.setChallenger(playerDao.getByUser(challenger).stream().filter(p-> p.getDivision().getType() == type).findFirst().get());
        challenge.setOpponent(playerDao.getByUser(opponent).stream().filter(p -> p.getDivision().getType() == type).findFirst().get());
        challenge.setSlot(slot);
        challenge.setStatus(Status.NOTIFY);
        Collection<Challenge> challenges = dao.get();
        //Check Dups
        boolean dup = false;
        for (Challenge c : challenges) {
            if (c.getChallenger().equals(challenge.getChallenger()) &&
                    c.getOpponent().equals(challenge.getOpponent()) &&
                    c.getSlot().equals(challenge.getSlot())
                    ) {
                logger.warn("Trying to create a request for one that already exists:" + c.getId());
                dup = true;
            }
        }
        if (!dup)
            dao.requestChallenge(challenge);
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
            c.setStatus(Status.NOTIFY);
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
        return slotDao.get(LocalDate.parse(date).atStartOfDay()).stream().sorted(new Comparator<Slot>() {
            @Override
            public int compare(Slot o1, Slot o2) {
                return o1.getLocalDateTime().compareTo(o2.getLocalDateTime());
            }
        }).collect(Collectors.toList());
    }

    public List<UserChallengeGroup> getPendingChallenges(User u) {
        return getChallenges(u, Status.PENDING);
    }

    public List<UserChallengeGroup> getNeedNotifyChallenges(User u) {
        return getChallenges(u, Status.NOTIFY);
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

}
