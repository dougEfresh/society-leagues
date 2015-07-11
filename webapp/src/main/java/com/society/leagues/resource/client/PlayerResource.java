package com.society.leagues.resource.client;

import com.society.leagues.adapters.PlayerAdapter;
import com.society.leagues.adapters.UserAdapter;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.dao.ChallengeDao;
import com.society.leagues.dao.PlayerDao;
import com.society.leagues.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PlayerResource {

    @Autowired PlayerDao playerDao;
    @Autowired UserDao userDao;
    @Autowired UserResource userResource;
    @Autowired ChallengeDao challengeDao;

    @RequestMapping(value = "/api/admin/challenge/modify/{userId}/{hc}", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserAdapter modifyChallengeHandicap(Principal principal, @PathVariable Integer userId, @PathVariable String hc) {
        User user = userDao.get(principal.getName());
        if (user == null || user.getRole() != Role.ADMIN)  {
            throw new RuntimeException("Invalid Admin User");
        }
        Player old  = playerDao.get().stream().filter(p->p.getDivision().isChallenge()).
                filter(p->p.getUserId().equals(userId)).
                filter(p->p.getEnd() == null).findFirst().orElseGet(null);
        if (old == null) {
            throw new RuntimeException("No player find for " + userId);
        }
        old.setHandicap(Handicap.valueOf(hc));
        Integer oldId = old.getId();
        Player newPlayer = playerDao.create(old);
        LocalDateTime now = LocalDateTime.now();

        List<Challenge> challenges = challengeDao.get().stream().
                filter(c -> c.getChallenger().getId().equals(oldId) || c.getOpponent().getId().equals(oldId)).
                filter(c -> c.getSlot().getLocalDateTime().isAfter(now)).
                collect(Collectors.toList());

        for (Challenge challenge : challenges) {
            if (challenge.getOpponent().getId().equals(oldId)) {
                challenge.setOpponent(newPlayer);
                challengeDao.modifyChallenge(challenge);
            }

            if (challenge.getChallenger().getId().equals(oldId)) {
                challenge.setChallenger(newPlayer);
                challengeDao.modifyChallenge(challenge);
            }
        }

        return userResource.get(userId);
    }
}
