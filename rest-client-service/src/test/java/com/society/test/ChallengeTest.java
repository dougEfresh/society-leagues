package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.SchemaData;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.ChallengeApi;
import com.society.leagues.client.api.UserApi;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.DivisionType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@IntegrationTest(value = {"server.port:0","daemon:true","debug:true"})
public class ChallengeTest extends TestBase  implements ChallengeApi {
    
    ChallengeApi api;
    UserApi userApi;
    
    @Override
    public void setup() throws Exception {
        super.setup();
        api = ApiFactory.createApi(ChallengeApi.class,authenticate(Role.PLAYER),baseURL);
        userApi = ApiFactory.createApi(UserApi.class,authenticate(Role.PLAYER),baseURL);
    }

    @Test
    public void testPotentials() throws Exception {
        for (User user : userApi.get()) {
            verifyUser(user);
        }
    }

    @Test
    public void testRequestChallenge() throws Exception {
        User user = SchemaData.challengeUsers.get(0);
        List<Player> players = getPotentials(user.getId());
        assertNotNull(players);
        Player opponent = players.get(0);
        Player challenger = user.getPlayers().stream().filter(p -> p.getDivision().getType() == opponent.getDivision().getType()).findFirst().orElseGet(null);
        Challenge challenge = new Challenge();
        Slot slot = Slot.getDefault(new Date()).get(0);
        challenge.setSlot(slot);
        challenge.setChallenger(challenger);
        challenge.setOpponent(opponent);
        challenge = requestChallenge(challenge);
        assertNotNull(challenge);
        assertNotNull(challenge.getId());
    }

    @Test
    public void testDupSlot() throws Exception {
        
    }

    @Override
    public Challenge requestChallenge(Challenge challenge) {
        return api.requestChallenge(challenge);
    }

    @Test
    public void testAcceptChallenge() throws Exception {
        Challenge challenge = create();
        challenge = acceptChallenge(challenge);
        assertNotNull(challenge);
        assertNotNull(challenge.getId());
    }

    @Override
    public Challenge acceptChallenge(Challenge challenge) {
        return api.acceptChallenge(challenge);
    }

    @Test
    public void testList() throws Exception {
        Challenge challenge = create();
        List<Challenge> challenges = listChallenges(challenge.getChallenger().getUser().getId());
        assertNotNull(challenges);
        assertFalse(challenges.isEmpty());
        assertTrue(challenges.size() == 1);
    }

    @Override
    public List<Challenge> listChallenges(Integer userId) {
        return api.listChallenges(userId);
    }

    @Override
    public Boolean cancelChallenge(Challenge challenge) {
        return null;
    }

    @Override
    public Challenge modifyChallenge(Challenge challenge) {
        return null;
    }

    @Override
    public List<Slot> slots(Date date) {
        return null;
    }
    
    static int COUNTER = 0;
    
    private Challenge create() {
        User user = SchemaData.challengeUsers.get(COUNTER++);
        List<Player> players = getPotentials(user.getId());
        assertNotNull(players);
        Player opponent = players.get(0);
        Player challenger = user.getPlayers().stream().filter(p -> p.getDivision().getType() == opponent.getDivision().getType()).findFirst().orElseGet(null);
        Challenge challenge = new Challenge();
        Slot slot = Slot.getDefault(new Date()).get(0);
        challenge.setSlot(slot);
        challenge.setChallenger(challenger);
        challenge.setOpponent(opponent);
        challenge = requestChallenge(challenge);
        assertNotNull(challenge);
        assertNotNull(challenge.getId());
        return challenge;
        
    }

    private void verifyUser(User challenger) {
        challenger = userApi.current(challenger.getId()).get(0);
        if (challenger.getPlayers() == null || challenger.getPlayers().isEmpty())
            return;


        List < Player > players = getPotentials(challenger.getId());
        Player ePlayer = challenger.getPlayers().stream().filter(p -> p.getDivision().getType() == DivisionType.EIGHT_BALL_CHALLENGE).findFirst().orElse(null);
        Player nPlayer = challenger.getPlayers().stream().filter(p -> p.getDivision().getType() == DivisionType.NINE_BALL_CHALLENGE).findFirst().orElse(null);

        assertNotNull(players);
        assertFalse(players.isEmpty());
        for (Player player : players) {
            if (ePlayer != null && player.getDivision().getType() == DivisionType.EIGHT_BALL_CHALLENGE) {
                assertTrue(
                        player.getHandicap().ordinal() <= ePlayer.getHandicap().ordinal()+3 
                                &&
                                player.getHandicap().ordinal() >=  ePlayer.getHandicap().ordinal()-3
                );
            }

            if (nPlayer != null && player.getDivision().getType() == DivisionType.NINE_BALL_CHALLENGE) {
                assertTrue(
                         player.getHandicap().ordinal() <= nPlayer.getHandicap().ordinal()+3
                                &&
                                player.getHandicap().ordinal() >=  nPlayer.getHandicap().ordinal()-3
                );
            }
        }
        
    }

    @Override
    public List<Player> getPotentials(Integer id) {
        return api.getPotentials(id);
    }
}
