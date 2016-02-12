package com.society.leagues.test;

import com.society.leagues.client.api.domain.*;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class TestPlayerResult  extends BaseTest {

    private static Logger logger = Logger.getLogger(TestUser.class);


    @Test
    public void testPlayerResult() {
        List<Season> seasons = seasonApi.active().stream().filter(s->!s.isChallenge()).collect(Collectors.toList());
        for (Season season : seasons) {
            seasonApi.schedule(season.getId());
            TeamMatch tm = teamMatchApi.matchesBySeasonSummary(season.getId()).values().iterator().next().stream().findAny().get();
            List<PlayerResult> playerResults = playerResultApi.add(tm.getId());
            for (PlayerResult playerResult : playerResults) {
                playerResult.setHomeRacks(1);
                playerResult.setAwayRacks(2);
                if (season.isScramble()) {
                    User h = teamApi.members(tm.getHome().getId()).getMembers().stream().filter(u->!u.equals(playerResult.getPlayerHome())).findAny().get();
                    User a = teamApi.members(tm.getAway().getId()).getMembers().stream().filter(u->!u.equals(playerResult.getPlayerAway())).findAny().get();
                    playerResult.setPlayerAwayPartner(a);
                    playerResult.setPlayerHomePartner(h);
                }
            }
            playerResults = playerResultApi.save(playerResults);
            for (PlayerResult playerResult : playerResults) {
                assertEquals(playerResult.getHomeRacks(),new Integer(1));
                assertEquals(playerResult.getAwayRacks(),new Integer(2));
                assertTrue(playerResult.getWinner().equals(playerResult.getPlayerAway()));
                assertTrue(playerResult.getLoser().equals(playerResult.getPlayerHome()));
                validatePlayer(playerResult.getPlayerHomeHandicap());
                validatePlayer(playerResult.getPlayerAwayHandicap());
                if (season.isScramble()) {
                    validatePlayer(playerResult.getPlayerHomeHandicapPartner());
                    validatePlayer(playerResult.getPlayerAwayHandicapPartner());
                }
            }
        }
    }

    private void validatePlayer(Handicap handicap){
        assertNotNull(handicap);
        assertTrue(handicap != Handicap.NA && handicap != Handicap.UNKNOWN);
    }

}
