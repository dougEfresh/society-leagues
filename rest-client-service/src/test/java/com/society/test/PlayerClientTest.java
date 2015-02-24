package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.api.domain.Player;
import com.society.leagues.dao.PlayerDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true, value = {"embedded=true", "generate=true"})
public class PlayerClientTest {

    @Autowired PlayerDao playerDao;

    @Test
    public void testMatch() {
        List<Player> players = playerDao.get().stream().filter( p -> p.getDivision().isChallenge()).collect(Collectors.toList());
        assertNotNull(players);

        for (Player player : players) {
            assertNotNull(player.getMatches());
            assertFalse(player.getMatches().isEmpty());
        }
    }

}
