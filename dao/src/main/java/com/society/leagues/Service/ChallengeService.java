package com.society.leagues.Service;

import com.society.leagues.client.api.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChallengeService  {

    @Autowired LeagueService leagueService;

    public Team createChallengeUser(final User user) {
        Season challenge = leagueService.findAll(Season.class).stream().parallel().filter(s -> s.getDivision().isChallenge()).findFirst().get();
        if (user.getHandicapSeasons().stream().filter(hs->hs.getSeason().getDivision().isChallenge()).count() == 0) {
            user.addHandicap(new HandicapSeason(Handicap.DPLUS,challenge));
        }
        leagueService.save(user);

        Team t = leagueService.findAll(Team.class).stream().parallel().filter(team->team.getName().equals(user.getName())).findFirst().orElse(null);
        if (t != null) {
            return t;
        }
        t = new Team(challenge,user.getName());
        return leagueService.save(t);
    }
}
