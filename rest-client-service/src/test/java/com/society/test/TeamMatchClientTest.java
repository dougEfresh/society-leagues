package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.api.MatchApi;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.client.api.domain.Team;
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
public class TeamMatchClientTest implements MatchApi {

    @Autowired MatchDao matchDao;

    @Test
    public void testGet() throws Exception {
        List<TeamMatch> teamMatches = get();
        assertNotNull(teamMatches);
        assertFalse(teamMatches.isEmpty());
    }

    @Override
    public List<TeamMatch> get() {
        return matchDao.get();
    }

    @Test
    public void testId() throws Exception {
        List<TeamMatch> teamMatches = get();
        assertNotNull(teamMatches);
        for (TeamMatch teamMatch : teamMatches) {
            TeamMatch m = get(teamMatch.getId());
            assertNotNull(m);
            assertTrue(m.getDivision().isChallenge());
        }
    }

    @Override
    public TeamMatch get(Integer id) {
        return matchDao.get(id);
    }

    @Test
    public void testTeam() throws Exception {
        List<TeamMatch> matches = get();
        for (TeamMatch match : matches) {
            List<TeamMatch> teamTeamMatches = getByTeam(match.getHome());
            assertNotNull(teamTeamMatches);
            assertFalse(teamTeamMatches.isEmpty());
            TeamMatch found  = teamTeamMatches.stream().filter(tm -> tm.getMatchDate() != null).filter(tm -> tm.getId().equals(match.getId())).findFirst().orElse(null);
            assertNotNull(found);
        }
    }

    @Override
    public List<TeamMatch> getByTeam(Team team) {
        return matchDao.getByTeam(team);
    }

    @Test
    public void testIds() throws Exception {
        TeamMatch m = get().stream().findFirst().get();
        List<TeamMatch> teamMatches = get(Arrays.asList(m.getId()));
        assertNotNull(teamMatches);
        assertFalse(teamMatches.isEmpty());
        assertTrue(teamMatches.get(0).getId().equals(m.getId()));
    }

    @Override
    public List<TeamMatch> get(List<Integer> id) {
        return matchDao.get(id);
    }
}
