package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.api.MatchApi;
import com.society.leagues.client.api.domain.Match;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.division.DivisionType;
import com.society.leagues.dao.MatchDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true, value = {"embedded=true", "generate=true"})
public class MatchClientTest implements MatchApi {

    @Autowired MatchDao matchDao;

    @Test
    public void testGet() throws Exception {
        List<Match> matches = get();
        assertNotNull(matches);
        assertFalse(matches.isEmpty());
    }

    @Override
    public List<Match> get() {
        return matchDao.get();
    }

    @Test
    public void testId() throws Exception {
        List<Match> matches = get();
        assertNotNull(matches);
        for (Match match : matches) {
            Match m = get(match.getId());
            assertNotNull(m);
            assertTrue(m.getDivision().isChallenge());
        }
    }

    @Override
    public Match get(Integer id) {
        return matchDao.get(id);
    }

    @Test
    public void testTeam() throws Exception {
        List<Match> matches = get();
        for (Match match : matches) {
            List<Match> teamMatches = getByTeam(match.getHome());
            assertNotNull(teamMatches);
            assertFalse(teamMatches.isEmpty());
            boolean found = false;
            for (Match teamMatch : teamMatches) {
                if (teamMatch.getId().equals(match.getId()))
                    found = true;
            }
            assertTrue(found);
        }
    }

    @Override
    public List<Match> getByTeam(Team team) {
        return matchDao.getByTeam(team);
    }

    @Test
    public void testIds() throws Exception {
        Match m = get().stream().findFirst().get();
        List<Match> matches = get(Arrays.asList(m.getId()));
        assertNotNull(matches);
        assertFalse(matches.isEmpty());
        assertTrue(matches.get(0).getId().equals(m.getId()));
    }

    @Override
    public List<Match> get(List<Integer> id) {
        return matchDao.get(id);
    }
}
