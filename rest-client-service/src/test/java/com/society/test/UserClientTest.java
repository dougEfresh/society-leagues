package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.dao.UserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true, value = {"embedded=true", "generate=true"})
public class UserClientTest {
    @Autowired UserDao api;

    @Test
    public void testCurrent() throws Exception {
        List<User> users = api.get();
        assertNotNull(users);
        assertFalse(users.isEmpty());
    }

    @Test
    public void testChallengers() throws Exception {
        List<User> users = api.get();
        for (User user : users) {
            List<Player> players = user.getPlayers().stream().filter( p -> p.getDivision().isChallenge()).collect(Collectors.toList());
            assertNotNull(players);
            assertFalse(players.isEmpty());

            for (Player player : players) {
                assertNotNull(player.getTeam());

                assertNotNull(player.getChallenges());
                assertFalse(player.getChallenges().isEmpty());

                assertNotNull(player.getTeamMatches());
                assertFalse(player.getTeamMatches().isEmpty());
                assertNotNull(player.getUserId());

            }
        }
    }

    @Test
    public void testPlayers() throws Exception {
        List<User> users = api.get();
        assertNotNull(users);
        assertFalse(users.isEmpty());
        users = users.stream().filter(u -> u.getLogin().contains("login")).collect(Collectors.toList());
        assertNotNull(users);
        assertFalse(users.isEmpty());

        for (User user : users) {
            assertFalse(user.getPlayers().isEmpty());
            assertFalse(user.getTeams().isEmpty());
            assertFalse(user.getSeasons().isEmpty());
            assertFalse(user.getDivisions().isEmpty());
        }
    }
}
