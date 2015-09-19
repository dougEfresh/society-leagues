package com.society.leagues.resource;

import com.society.leagues.Service.LeagueService;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.email.EmailSender;
import com.society.leagues.mongo.ChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping(value = "/api/challenge")
@SuppressWarnings("unused")
public class ChallengeResource {

    @Autowired LeagueService leagueService;
    @Autowired EmailSender emailSender;
    @Value("${service-url:http://leaguesdev.societybilliards.com}") String serviceUrl;
    static final Logger logger = LoggerFactory.getLogger(ChallengeResource.class);

    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Challenge create(@RequestBody Challenge challenge, Principal principal, HttpServletRequest request) {
        if (principal == null) {
            return new Challenge();
        }
        User u = leagueService.findByLogin(principal.getName());
        if (challenge.getChallenger().getMembers().contains(u) || challenge.getOpponent().getMembers().contains(u) || u.isAdmin()) {
            return leagueService.save(challenge);
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

        if (u.getId().equals(challenge.getChallenger().getId()) || u.getId().equals(challenge.getId()) || u.isAdmin()) {
            challenge.setStatus(Status.ACCEPTED);
            Challenge acceptedChallenge = leagueService.save(challenge);
            Team challengerTeam = acceptedChallenge.getChallenger();
            Team opponentTeam = acceptedChallenge.getOpponent();
            TeamMatch tm = new TeamMatch(challengerTeam,opponentTeam,acceptedChallenge.getAcceptedSlot().getLocalDateTime());
            return leagueService.save(tm);

        }
        //TODO throw exception
        return null;
    }

    @RequestMapping(value = "/decline", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
     public Challenge decline(@RequestBody Challenge challenge, Principal principal, HttpServletRequest request) {
         if (principal == null) {
            return null;
         }
         Challenge c = leagueService.findOne(challenge);
         c.setStatus(Status.CANCELLED);
         c.setAcceptedSlot(null);
         return leagueService.save(c);
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<Challenge> get(Principal principal) {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        return leagueService.findAll(Challenge.class).stream().filter(c->c.getSlots().get(0).getLocalDateTime().isAfter(yesterday)).collect(Collectors.toList());
    }

    private void sendEmail(User to, User from, Status status, Challenge challenge, String message) {
        String subject;
        String body;

        switch (status) {
            case NOTIFY:
                subject = "Society Leagues - Challenge Request - " + from.getName();
                body =  String.format(
				      "You've been challenged! %s respectfully yet aggressively requests a match with you.\n" +
                                "Click %s#/app/challenge/main for details\n",
                        from.getName(), serviceUrl);
                break;
            case ACCEPTED:
                subject = "Society Leagues - Challenge Accepted - " + from.getName();
                body = String.format("Your challenge from %s has been accepted.\n" +
                        "See %s#/app/challenge/main for details."
                        , from.getName(), serviceUrl);

                break;
            case CANCELLED:
                subject = "Society Leagues - Challenge Declined - " + from.getName();
                body = String.format("%s\n*****\n%s has declined the challenge.\n" +
                                "Don't worry! You can click %s#/app/challenge/main to challenge someone else!\n",
                                message == null ? "": message,
                        from.getName(), serviceUrl);

                break;
            default:
                logger.warn("Got email request for unknown status " + status);
                return;

        }
        logger.info("Creating an email to " + to.getEmail() + " subject:" + subject + " ");
        emailSender.email(to.getEmail(),subject,body);

    }

}
