package com.society.leagues.resource;

import com.society.leagues.client.api.domain.*;
import com.society.leagues.mongo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@RestController
@RequestMapping(value = "/api")
public class ChallengeResource {

    @Autowired ChallengRepository challengRepository;
    @Autowired UserRepository userRepository;
    @Autowired SlotRepository slotRepository;
    @Autowired TeamMatchRepository teamMatchRepository;

    @RequestMapping(value = "/challenge/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Challenge create(@RequestBody Challenge challenge, Principal principal, HttpServletRequest request) {
        if (principal == null) {
            return new Challenge();
        }
        User u = userRepository.findByLogin(principal.getName());
        if (u.getId().equals(challenge.getChallenger().getId()) || u.getId().equals(challenge.getId()) || u.isAdmin()) {
            challenge = challengRepository.save(challenge);
            return challengRepository.findOne(challenge.getId());
        }
        //TODO throw exception
        return new Challenge();
    }

    @RequestMapping(value = "/challenge/accept", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public TeamMatch accept(@RequestBody Challenge challenge, Principal principal, HttpServletRequest request) {
        if (principal == null) {
            return null;
        }
        if (challenge.getAcceptedSlot() == null) {
            return null;
        }
        User u = userRepository.findByLogin(principal.getName());
        //challenge = hydrate(challenge);

        if (u.getId().equals(challenge.getChallenger().getId()) || u.getId().equals(challenge.getId()) || u.isAdmin()) {
            challenge.setStatus(Status.ACCEPTED);
            Challenge acceptedChallenge = challengRepository.save(challenge);
            Team challengerTeam = acceptedChallenge.getChallenger().getTeams().stream().filter(s->s.getSeason().getDivision() == Division.NINE_BALL_CHALLENGE).findFirst().orElse(null);
            Team opponentTeam = acceptedChallenge.getOpponent().getTeams().stream().filter(s->s.getSeason().getSeasonStatus() == Status.ACTIVE).filter(s->s.getSeason().getDivision() == Division.NINE_BALL_CHALLENGE).findFirst().orElse(null);
            TeamMatch tm = new TeamMatch(challengerTeam,opponentTeam,challengerTeam.getSeason(),acceptedChallenge.getAcceptedSlot().getLocalDateTime());
            tm = teamMatchRepository.save(tm);
            return teamMatchRepository.findOne(tm.getId());
        }
        //TODO throw exception
        return null;
    }

     @RequestMapping(value = "/challenge/decline", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
     public Challenge decline(@RequestBody Challenge challenge, Principal principal, HttpServletRequest request) {
         if (principal == null) {
            return null;
         }
         Challenge c = challengRepository.findOne(challenge.getId());
         c.setStatus(Status.CANCELLED);
         c.setAcceptedSlot(null);
         c = challengRepository.save(c);
         return challengRepository.findOne(c.getId());
    }
}
