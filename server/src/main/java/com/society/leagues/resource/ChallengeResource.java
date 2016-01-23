package com.society.leagues.resource;

import com.society.leagues.service.ChallengeService;
import com.society.leagues.service.LeagueService;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping(value = "/api/challenge")
@SuppressWarnings("unused")
public class ChallengeResource {

    @Autowired LeagueService leagueService;
    @Autowired EmailService emailService;
    @Value("${service-url:http://leaguesdev.societybilliards.com}") String serviceUrl;
    static final Logger logger = LoggerFactory.getLogger(ChallengeResource.class);
    @Autowired ChallengeService challengeService;

    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Challenge create(@RequestBody Challenge challenge, Principal principal, HttpServletRequest request) {
        if (principal == null) {
            return new Challenge();
        }
        User u = leagueService.findByLogin(principal.getName());
        Team challenger = leagueService.findOne(challenge.getChallenger());
        Team opponent = leagueService.findOne(challenge.getOpponent());
        if (challenge.isBroadcast()) {
            return leagueService.save(challenge);
        }
        if (challenger.getChallengeUser().equals(u) || opponent.getChallengeUser().equals(u) || u.isAdmin()) {
            Challenge c = leagueService.save(challenge);
            sendEmail(c.getUserOpponent(),c.getUserChallenger(),Status.NOTIFY,c,null);
            return c;
        }

        //TODO throw exception
        return new Challenge();
    }

    @RequestMapping(value = "/accept", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public TeamMatch accept(@RequestBody Challenge challenge, Principal principal, HttpServletRequest request) {
        if (principal == null) {
            return null;
        }
        if (challenge.getAcceptedSlot() == null) {
            return null;
        }
        User u = leagueService.findByLogin(principal.getName());
        Slot accepted = leagueService.findOne(challenge.getAcceptedSlot());
        challenge = leagueService.findOne(challenge);

        sendEmail(challenge.getUserChallenger(), challenge.getUserOpponent(), Status.ACCEPTED, null, null);
        if (u.equals(challenge.getChallenger().getChallengeUser()) || u.equals(challenge.getOpponent().getChallengeUser()) || u.isAdmin()) {
            challenge.setStatus(Status.ACCEPTED);
            challenge.setAcceptedSlot(accepted);
            leagueService.save(challenge);
            return challengeService.accept(challenge);
        }
        //TODO throw exception
        return null;
    }

    @RequestMapping(value = {"/decline", "/cancel"}, method = {RequestMethod.POST, RequestMethod.DELETE}, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Challenge decline(@RequestBody Challenge challenge, Principal principal, HttpServletRequest request) {
         if (principal == null) {
            return null;
         }
        User u = leagueService.findByLogin(principal.getName());
        Challenge c = leagueService.findOne(challenge);
        challengeService.cancel(c);
        if (c.getUserChallenger().equals(u)) {
            sendEmail(c.getUserOpponent(), c.getUserChallenger(), Status.CANCELLED, null, challenge.getMessage());
        } else {
            sendEmail(c.getUserChallenger(), c.getUserOpponent(), Status.CANCELLED, null, challenge.getMessage());
        }
        return c;
    }

    @RequestMapping(value = {"/slots"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Collection<Slot> slots(Principal principal, HttpServletRequest request) {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        return leagueService.findAll(Slot.class).stream().filter(s->s.getLocalDateTime().isAfter(yesterday)).filter(s->s.getAllocated() <4).collect(Collectors.toList());
    }

    @RequestMapping(value = {"/users"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Collection<Team> challengeUsers(Principal principal) {
        User user = leagueService.findByLogin(principal.getName());

        return leagueService.findAll(Team.class).parallelStream().filter(Team::isChallenge).collect(Collectors.toList());
    }

    @RequestMapping(value = {"/", "","get"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<Challenge> get(Principal principal) {
        LocalDate yesterday = LocalDateTime.now().minusDays(1).toLocalDate();
        List<Challenge> challenges =  leagueService.findAll(Challenge.class).stream().
                filter(c -> c.getLocalDate().isAfter(yesterday)).
                filter(c -> !c.isCancelled()).
                collect(Collectors.toList()
                );
        challenges.sort(new Comparator<Challenge>() {
            @Override
            public int compare(Challenge challenge, Challenge t1) {
                return challenge.getLocalDate().compareTo(t1.getLocalDate());
            }
        });
        return challenges;
    }

    @RequestMapping(value = {"/upcoming"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<TeamMatch> upcomingChallenges() {
        LocalDateTime now  = LocalDateTime.now().toLocalDate().atStartOfDay().minusDays(1);
        List<Challenge> challenges =
                leagueService.findAll(Challenge.class).parallelStream().filter(c -> c.getLocalDate().atStartOfDay().isAfter(now)).filter(c -> c.getStatus() == Status.ACCEPTED).filter(c -> c.getTeamMatch() != null).collect(Collectors.toList());
        List<TeamMatch> matches = new ArrayList<>();
        challenges.forEach(c->matches.add(c.getTeamMatch()));
        matches.sort(new Comparator<TeamMatch>() {
            @Override
            public int compare(TeamMatch o1, TeamMatch o2) {
                return o1.getMatchDate().compareTo(o2.getMatchDate());
            }
        });
        return matches;
    }

    @RequestMapping(value = {"/date/{date}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<Team> getUsersAvailableOnDate(@PathVariable String date, Principal principal) {
        try {
            LocalDate dt = LocalDate.parse(date);
            Collection<Team> teams = challengeUsers(principal);
            User u  = leagueService.findByLogin(principal.getName());
            List<Challenge> challenges = leagueService.findAll(Challenge.class).stream().parallel().
                    filter(c -> c.getLocalDate().atStartOfDay().toLocalDate().isEqual(dt)).
                    filter(c->c.getStatus() == Status.ACCEPTED)
                    .filter(c->c.getUserChallenger().equals(u) || c.getUserOpponent().equals(u))
                    .collect(Collectors.toList());
            List<Team> available = new ArrayList<>();
            for (Team team : teams) {
                if (challenges.stream().filter(c->c.hasTeam(team)).count() == 0) {
                    if (team.getChallengeUser().hasSeason(team.getSeason()))
                        available.add(team);
                }
            }
            return available.stream().sorted((o1, o2) -> o1.getName().compareTo(o2.getName())).collect(Collectors.toList());
        } catch (Exception t) {
            logger.error(t.getMessage(),t);
        }
        return Collections.emptyList();
    }

    @RequestMapping(value = {"/slots/{date}/{id}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<Slot> getAvailableSlotsOnDate(@PathVariable String id, @PathVariable String date, Principal principal) {
        try {
            LocalDate dt = LocalDate.parse(date);
            List<Slot> slots = leagueService.findAll(Slot.class).parallelStream().filter(s->s.getLocalDateTime().toLocalDate().isEqual(dt)).collect(Collectors.toList());
            slots.sort((o1, o2) -> o1.getLocalDateTime().compareTo(o2.getLocalDateTime()));
            return slots;
        } catch (Exception t) {
            logger.error(t.getMessage(),t);
        }
        return Collections.emptyList();
    }

    @RequestMapping(value = {"/user/{id}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<Challenge> getByUser(Principal principal, @PathVariable String id) {
        User u = leagueService.findOne(new User(id));
        LocalDate now = LocalDate.now().minusDays(1);
        return get(principal).stream().parallel()
                .filter(c -> c.hasUser(u))
                .filter(c->c.getLocalDate().isEqual(now) || c.getLocalDate().isAfter(now))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = {"/results"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Map<String,List<PlayerResult>> results(Principal principal) {
        List<PlayerResult> results;
        results = leagueService.findAll(PlayerResult.class).stream().parallel()
                .sorted(new Comparator<PlayerResult>() {
                    @Override
                    public int compare(PlayerResult teamMatch, PlayerResult t1) {
                        return t1.getMatchDate().compareTo(teamMatch.getMatchDate());
                    }
                }).collect(Collectors.toList());
        Map<String,List<PlayerResult>> group = results.stream().collect(Collectors.
                groupingBy(tm -> tm.getTeamMatch().getMatchDate().toLocalDate().toString())
        );
        return (Map<String,List<PlayerResult>>) new TreeMap<>(group);
    }


    private void sendEmail(User to, User from, Status status, Challenge challenge, String message) {
        String subject;
        String body;

        switch (status) {
            case NOTIFY:
                subject = "Society Leagues - Challenge Request - " + from.getName();
                body =  String.format(
				      "You've been challenged! %s respectfully yet aggressively requests a match with you.\n" +
                                "Click %s#/app/challenge for details\n",
                        from.getName(), serviceUrl);
                break;
            case ACCEPTED:
                subject = "Society Leagues - Challenge Accepted - " + from.getName();
                body = String.format("Your challenge from %s has been accepted.\n" +
                        "See %s#/app/challenge for details."
                        , from.getName(), serviceUrl);

                break;
            case CANCELLED:
                subject = "Society Leagues - Challenge Declined - " + from.getName();
                body = String.format("%s\n*****\n%s has declined the challenge.\n" +
                                "Don't worry! You can click %s#/app/challenge to challenge someone else!\n",
                                message == null ? "": message,
                        from.getName(), serviceUrl);

                break;
            default:
                logger.warn("Got email request for unknown status " + status);
                return;

        }
        logger.info("Creating an email to " + to.getEmail() + " subject:" + subject + " ");
        emailService.email(to.getEmail(),subject,body);
    }

}
