package com.society.leagues.Service;

import com.society.leagues.client.api.domain.PlayerResult;
import com.society.leagues.client.api.domain.TeamMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ResultService {
    @Autowired LeagueService leagueService;

    public PlayerResult createOrModify(PlayerResult playerResult) {

        if (playerResult.getSeason().getDivision().isChallenge()) {
            TeamMatch tm = playerResult.getTeamMatch();
            tm.setAwayRacks(playerResult.getAwayRacks());
            tm.setHomeRacks(playerResult.getHomeRacks());
            tm.setSetAwayWins(playerResult.getAwayRacks());
            tm.setSetHomeWins(playerResult.getHomeRacks());
            leagueService.save(tm);
        }

        return leagueService.save(playerResult);

    }

     public TeamMatch createOrModify(TeamMatch teamMatch) {

        if (teamMatch.getSeason().getDivision().isChallenge()) {
            // don't modify team matches for challenges
            return  teamMatch;
        }

        return leagueService.save(teamMatch);

    }
}
