package com.society.leagues.resource.client;
import com.society.leagues.adapters.*;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.email.EmailSender;
import com.society.leagues.email.EmailService;
import com.society.leagues.email.EmailTaskRunner;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired EmailService emailService;
    @Autowired UserResource userResource;
    @Autowired SeasonDao seasonDao;
    @Autowired TeamDao teamDao;


    @RequestMapping(value = "/challenge/signup/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserAdapter signup(@PathVariable Integer userId) {
        User u = userDao.get(userId);
        if (u == null) {
            logger.info("No user for " + userId);
            return UserAdapter.DEFAULT_USER;
        }
        //TODO check if already challenge user
        UserAdapter userAdapter =  userResource.get(u.getId());
        if (userAdapter.isChallenge()) {
            return userAdapter;
        }
        List<Season> challengeSeasons = seasonDao.getActive().stream().filter(s->s.getDivision().isChallenge()).collect(Collectors.toList());
        Team team = new Team();
        team.setName(u.getName());
        if (teamDao.get(u.getName()) == null) {
            team = teamDao.create(team);
        } else {
            team = teamDao.get(u.getName());
        }

        for (Season challengeSeason : challengeSeasons) {
            Player player = new Player();
            player.setTeam(team);
            player.setUser(u);
            player.setDivision(challengeSeason.getDivision());
            player.setSeason(challengeSeason);
            if (challengeSeason.getDivision().getType() == DivisionType.NINE_BALL_CHALLENGE) {
                player.setHandicap(Handicap.DPLUS);
            }
            if (challengeSeason.getDivision().getType() == DivisionType.EIGHT_BALL_CHALLENGE) {
                player.setHandicap(Handicap.FOUR);
            }
            player.setStart(new Date());
            playerDao.create(player);
        }

        return userResource.get(u.getId());
    }

    @RequestMapping(value = "/challenge/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Status,List<UserChallengeGroup>> getChallenges(@PathVariable Integer userId) {
        User u = userDao.get(userId);
        Map<Status,List<UserChallengeGroup>> challenges = new HashMap<>();
        for (Status status : Status.values()) {
            challenges.put(status,getUserChallengeGroups(u, status));
        }
        challenges.put(Status.SENT,
                challenges.get(Status.PENDING).stream().
                        filter(c -> c.getChallenger().equals(u.getId())).
                        collect(Collectors.toList())
        );
        challenges.put(Status.PENDING,
                challenges.get(Status.PENDING).stream().
                        filter(c -> !c.getChallenger().equals(u.getId())).
                        collect(Collectors.toList())
        );
        challenges.put(Status.ACCEPTED,
                challenges.get(Status.ACCEPTED).stream().
                        filter(c -> !c.getDate().isBefore(LocalDate.now())).
                        collect(Collectors.toList())
        );
        challenges.put(Status.CANCELLED,Collections.emptyList());

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


    @RequestMapping(value = {"/challenge/cancel/{userId}","/challenge/cancelled/{userId}"},
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
  public UserAdapter cancel(@PathVariable Integer userId, @RequestBody UserChallengeGroup challengeGroup) {
        User user = userDao.get(userId);
        Challenge c = challengeGroup.challenges().get(0);
        User opponent = userDao.get(dao.get(c.getId()).getOpponent().getUserId());
        User challenger = userDao.get(dao.get(c.getId()).getChallenger().getUserId());
        dao.cancel(challengeGroup.challenges());
        if (user.equals(challenger)) {
            sendEmail(opponent, challenger, Status.CANCELLED);
        } else {
            sendEmail(challenger, opponent, Status.CANCELLED);
        }
        return userResource.get(user.getLogin());
    }

    @RequestMapping(value = {"/challenge/accept/{userId}", "/challenge/accepted/{userId}"},
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserAdapter accept(@PathVariable Integer userId, @RequestBody Challenge challenge) {
        User user = userDao.get(userId);
        final Challenge c  = dao.get(challenge.getId());
        if (user == null || c == null) {
            return UserAdapter.DEFAULT_USER;
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
        sendEmail(opponent, user, Status.ACCEPTED);
        return userResource.get(user.getLogin());
    }

    private void sendEmail(User to, User from, Status status) {
        String subject;
        String body;
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
        logger.info("Creating an email to " + to.getName() + " subject:" + subject + " ");
        taskRunner = new EmailTaskRunner(emailSender,subject,body,to.getEmail());
        emailService.add(taskRunner);
    }

    @RequestMapping(value = "/challenge/request",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserAdapter  requestChallenge(@RequestBody ChallengeRequest request) {
        logger.info("Got request for challenge " + request);
        User u = userDao.get(request.getChallenger().getId());
        User opponent = userDao.get(request.getOpponent().getId());

        for (Slot slot : request.getSlots()) {
            if (request.isEight())
                createChallenge(u,opponent,DivisionType.EIGHT_BALL_CHALLENGE,slot);

            if (request.isNine())
                createChallenge(u,opponent,DivisionType.NINE_BALL_CHALLENGE,slot);
        }

        sendEmail(opponent,u,Status.NOTIFY);
        return userResource.get(u.getLogin());
    }

    private void createChallenge(User challenger, User opponent, DivisionType type, Slot slot) {
        Challenge challenge = new Challenge();
        challenge.setChallenger(playerDao.getByUser(challenger).stream().filter(p-> p.getDivision().getType() == type).findFirst().get());
        challenge.setOpponent(playerDao.getByUser(opponent).stream().filter(p -> p.getDivision().getType() == type).findFirst().get());
        challenge.setSlot(slot);
        challenge.setStatus(Status.PENDING);
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

    @RequestMapping(value = "/challenge/slots",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public List<Slot> getSlots() {
        LocalDate now = LocalDate.now().with(DayOfWeek.SUNDAY);
        LocalDate end = LocalDate.now().plusDays(40);
        List<Slot> slots = new ArrayList<>();
        while(now.isBefore(end)) {
            slots.addAll(slotDao.get(now.atStartOfDay()).stream().sorted(new Comparator<Slot>() {
                @Override
                public int compare(Slot o1, Slot o2) {
                    return o1.getLocalDateTime().compareTo(o2.getLocalDateTime());
                    }
            }).collect(Collectors.toList()));
            now = now.plusDays(7);
        }
        return slots;
    }
}
