package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.api.ChallengeApi;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.DivisionType;
import com.society.leagues.dao.ChallengeDao;
import com.society.leagues.dao.PlayerDao;
import com.society.leagues.dao.UserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true, value = {"embedded=true", "generate=true"})
public class ChallengeTest implements ChallengeApi {
    private static Logger logger = LoggerFactory.getLogger(ChallengeTest.class);
    @Autowired ChallengeDao api;
    @Autowired UserDao userApi;
    @Autowired PlayerDao playerApi;

    @Test
    public void testPotentials() throws Exception {
        userApi.get().forEach(this::verifyUser);
    }

    @Test
    public void testRequestChallenge() throws Exception {
        User challenger = userApi.get("login4@example.com");
        List<User> users = getPotentials(challenger.getId());
        assertNotNull(users);
        assertFalse(users.isEmpty());
        User opponent = users.get(0);
        Player ch = challenger.getPlayers().stream().filter(p -> p.getDivision().getType() == DivisionType.NINE_BALL_CHALLENGE).findFirst().get();
        Player op = opponent.getPlayers().stream().filter(p -> p.getDivision().getType() == ch.getDivision().getType()).findFirst().orElseGet(null);
        Challenge challenge = new Challenge();
        Slot slot = Slot.getDefault(new Date()).get(0);
         TeamMatch teamMatch = new TeamMatch();
        teamMatch.setHome(ch.getTeam());
        teamMatch.setAway(op.getTeam());
        teamMatch.setDivision(ch.getDivision());
        teamMatch.setSeason(ch.getSeason());
        challenge.setStatus(Status.PENDING);
        challenge.setTeamMatch(teamMatch);

        challenge = requestChallenge(challenge);
        assertNotNull(challenge);
        assertNotNull(challenge.getId());
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
        List<Challenge> challenges = listChallenges(challenge.getTeamMatch().getHome().getId());
        assertNotNull(challenges);
        assertFalse(challenges.isEmpty());
        boolean found = false;
        for (Challenge c : challenges) {
            if (c.equals(challenge))
                found = true;
        }

        assertTrue(found);
    }

    @Override
    public List<Challenge> listChallenges(Integer userId) {
        return api.listChallenges(userId);
    }

    @Test
    public void testCancel() throws Exception {
        Challenge c = create();
        assertTrue(cancelChallenge(c));
    }

    @Override
    public Boolean cancelChallenge(Challenge challenge) {
        return api.cancelChallenge(challenge);
    }

    @Test
    public void testModify() throws Exception {
        Challenge challenge = create();
        Date d = new Date();
        challenge.setStatus(Status.ACTIVE);
        challenge = modifyChallenge(challenge);
        assertNotNull(challenge);
        assertTrue(challenge.getStatus() == Status.ACTIVE);
    }

    @Override
    public Challenge modifyChallenge(Challenge challenge) {
        return api.modifyChallenge(challenge);
    }

    @Override
    public List<Slot> slots(Date date) {
        return null;
    }

    @Test
    public void testByPlayer() throws Exception {
        List<Player> challengers = playerApi.get().stream().filter(p -> p.getDivision().isChallenge()).collect(Collectors.toList());
        for (Player challenger : challengers) {
            List<Challenge> challenges = getByTeam(challenger.getId());
            assertNotNull(challenges);
            assertFalse(challenges.isEmpty());
        }
    }

    @Override
    public List<Challenge> getByTeam(@PathVariable(value = "id") Integer id) {
        return api.getByTeam(id);
    }

    static int COUNTER = 1;
    
    private Challenge create() {
        User user = userApi.get("login" + COUNTER++ + "@example.com");
        List<User> users = getPotentials(user.getId());
        assertNotNull(users);
        assertFalse(users.isEmpty());
        Player opponent = user.getPlayers().stream().findAny().get();
        Player challenger = user.getPlayers().stream().filter(p -> p.getDivision().getType() == opponent.getDivision().getType()).findFirst().orElseGet(null);
        Challenge challenge = new Challenge();
        TeamMatch teamMatch = new TeamMatch();
        teamMatch.setHome(challenger.getTeam());
        teamMatch.setAway(opponent.getTeam());
        teamMatch.setDivision(challenger.getDivision());
        teamMatch.setSeason(challenger.getSeason());
        challenge.setStatus(Status.PENDING);
        challenge.setTeamMatch(teamMatch);
        challenge = requestChallenge(challenge);
        assertNotNull(challenge);
        assertNotNull(challenge.getId());
        return challenge;
        
    }

    private void verifyUser(User challenger) {
        challenger = userApi.get(challenger.getId());

        if (challenger.getPlayers() == null || challenger.getPlayers().isEmpty())
            return;


        List <User> users = getPotentials(challenger.getId());
        Player ePlayer = challenger.getPlayers().stream().filter(p -> p.getDivision().getType() == DivisionType.EIGHT_BALL_CHALLENGE).findFirst().orElse(null);
        Player nPlayer = challenger.getPlayers().stream().filter(p -> p.getDivision().getType() == DivisionType.NINE_BALL_CHALLENGE).findFirst().orElse(null);

        assertNotNull(users);
        assertFalse(users.isEmpty());
        for (User opponent : users) {
            for (Player player : opponent.getPlayers()) {
                if (ePlayer != null && player.getDivision().getType() == DivisionType.EIGHT_BALL_CHALLENGE) {
                    assertTrue(
                            player.getHandicap().ordinal() <= ePlayer.getHandicap().ordinal() + 3
                                    &&
                                    player.getHandicap().ordinal() >= ePlayer.getHandicap().ordinal() - 3
                    );
                }

                if (nPlayer != null && player.getDivision().getType() == DivisionType.NINE_BALL_CHALLENGE) {
                    boolean check = player.getHandicap().ordinal() <= nPlayer.getHandicap().ordinal() + 3
                            &&      player.getHandicap().ordinal() >= nPlayer.getHandicap().ordinal() - 3;

                    if (!check)
                        logger.info("Player is not with in handicap range\n\n" + player.getHandicap().ordinal() + "\n\n" + nPlayer.getHandicap().ordinal());

                    assertTrue(check);
                }
            }
        }
        
    }

    @Override
    public List<User> getPotentials(Integer id) {
        return api.getPotentials(id);
    }
}
