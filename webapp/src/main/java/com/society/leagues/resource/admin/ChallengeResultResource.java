package com.society.leagues.resource.admin;


import com.society.leagues.adapters.PlayerResultAdapter;
import com.society.leagues.adapters.PlayerResultRawAdapter;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.dao.*;
import com.society.leagues.resource.client.PlayerResource;
import com.society.leagues.resource.client.ResultResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ChallengeResultResource {
    @Autowired ChallengeDao challengeDao;
    @Autowired UserDao userDao;
    @Autowired TeamMatchDao teamMatchDao;
    @Autowired TeamResultDao teamResultDao;
    @Autowired PlayerResultDao playerResultDao;
    @Autowired ResultResource resultResource;

    @RequestMapping(value = "/api/admin/challenge/result/{challengeId}/{challengerRacks}/{opponentRacks}", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<PlayerResultRawAdapter> challengeResult(
            Principal principal,
            @PathVariable Integer challengeId,
            @PathVariable Integer challengerRacks,
            @PathVariable Integer opponentRacks) {
        if (principal == null) {
              throw new RuntimeException("Not a valid admin user ");
        }
        User user = userDao.get(principal.getName());
        if (user == null || !user.isAdmin()) {
            throw new RuntimeException("Not a valid admin user ");
        }
        Challenge c = challengeDao.get(challengeId);
        if (c == null) {
            throw new RuntimeException("Invalid Challenge " + challengeId);
        }

        TeamMatch teamMatch = c.getTeamMatch();
        if (teamMatch == null) {
            teamMatch = new TeamMatch();
            teamMatch.setHome(c.getChallenger().getTeam());
            teamMatch.setAway(c.getOpponent().getTeam());
            teamMatch.setMatchDate(c.getSlot().getLocalDateTime());
            teamMatch.setSeason(c.getChallenger().getSeason());
            teamMatch = teamMatchDao.create(teamMatch);
        }
        c.setTeamMatch(teamMatch);
        challengeDao.updateTeamMatch(c);

        TeamResult teamResult = teamMatch.getResult();
        if (teamResult == null) {
            teamResult = new TeamResult();
            teamResult.setTeamMatch(teamMatch);
            teamResult.setHomeRacks(challengerRacks);
            teamResult.setAwayRacks(opponentRacks);
            teamResult = teamResultDao.create(teamResult);
        } else {
            teamResult.setTeamMatch(teamMatch);
            teamResult.setHomeRacks(challengerRacks);
            teamResult.setAwayRacks(opponentRacks);
            teamResultDao.modify(teamResult);
        }
        final Integer teamMatchId = teamMatch.getId();
        PlayerResult result = playerResultDao.get().stream().filter(r-> r.getTeamMatch().getId().equals(teamMatchId)).findFirst().orElse(null);
        if (result == null) {
            result = new PlayerResult();
            result.setHomeRacks(challengerRacks);
            result.setAwayRacks(opponentRacks);
            result.setPlayerHome(c.getChallenger());
            result.setPlayerAway(c.getOpponent());
            result.setTeamMatch(teamMatch);
            result.setMatchNumber(1);
            playerResultDao.create(result);
        } else {
            result.setHomeRacks(challengerRacks);
            result.setAwayRacks(opponentRacks);
            playerResultDao.modify(result);
        }

        return resultResource.getCurrentResults();
    }
}
