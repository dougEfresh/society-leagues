package com.society.leagues.resource;

import com.society.leagues.Service.LeagueService;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.mongo.ChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping(value = "/api/challenge")
@SuppressWarnings("unused")
public class ChallengeResource {

    @Autowired LeagueService leagueService;
    @Autowired ChallengeRepository challengeRepository;
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Challenge create(@RequestBody Challenge challenge, Principal principal, HttpServletRequest request) {
        if (principal == null) {
            return new Challenge();
        }
        User u = leagueService.findByLogin(principal.getName());
        //Hydrate the object with latest data
        challenge.setChallenger(leagueService.findOne(challenge.getChallenger()));
        challenge.setOpponent(leagueService.findOne(challenge.getOpponent()));
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
}
