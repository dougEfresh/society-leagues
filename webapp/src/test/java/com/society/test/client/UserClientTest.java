package com.society.test.client;

import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.dao.UserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
public class UserClientTest extends TestClientBase {
    @Autowired UserDao api;


    @Test
    public void testChallengers() throws Exception {
        List<User> users = get();
        for (User user : users) {
            List<Player> players = user.getPlayers().stream().filter( p -> p.getDivision().isChallenge()).collect(Collectors.toList());
            assertNotNull(players);
            if (players.isEmpty()) {
                assertFalse(players.isEmpty());
            }

            for (Player player : players) {
                assertNotNull(player.getTeam());

                assertNotNull(player.getChallenges());
                if (player.getChallenges().isEmpty()) {
                    assertFalse(player.getChallenges().isEmpty());
                }
                assertFalse(player.getChallenges().isEmpty());

                assertNotNull(player.getTeamMatches());
                assertFalse(player.getTeamMatches().isEmpty());
                assertNotNull(player.getUserId());

                for (TeamMatch teamMatch : player.getTeamMatches()) {

                }
            }
        }
    }

    @Test
    public void testMatch() {
        List<User> users = get();
        assertNotNull(users);
        for (User user : users) {
            for (Player player : user.getPlayers()) {
                assertNotNull(player.getTeamMatches());
                assertFalse(player.getTeamMatches().isEmpty());
                for (TeamMatch teamMatch : player.getTeamMatches().stream().filter(t -> t.getResult() != null).collect(Collectors.toList())) {
                    assertTrue(teamMatch.getWinnerRacks() > 0);
                    assertTrue(teamMatch.getLoserRacks() > 0);
                }
                assertNotNull(player.getTeamMatches());
            }
        }
    }


    @Test
    public void testPlayers() throws Exception {
        List<User> users = get();
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

    public List<User> get() {
        return api.get().stream().filter(u -> u.getLogin().contains("login")).collect(Collectors.toList());
    }
}
