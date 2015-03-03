package com.society.test.client;

import com.society.leagues.client.api.MatchApi;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.dao.TeamMatchDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class TeamMatchClientTest extends TestClientBase implements MatchApi {

    @Autowired
    TeamMatchDao teamMatchDao;

    @Test
    public void testGet() throws Exception {
        List<TeamMatch> teamMatches = get();
        assertNotNull(teamMatches);
        assertFalse(teamMatches.isEmpty());
    }

    @Override
    public List<TeamMatch> get() {
        return teamMatchDao.get();
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
        return teamMatchDao.get(id);
    }

    @Test
    public void testTeam() throws Exception {
        List<TeamMatch> matches = get().stream().filter(tm -> tm.getMatchDate() != null).collect(Collectors.toList());
        for (TeamMatch match : matches) {
            List<TeamMatch> teamMatches = getByTeam(match.getHome());
            assertNotNull(teamMatches);
            assertFalse(teamMatches.isEmpty());
            TeamMatch found  = teamMatches.stream().
                    filter(tm -> tm.getMatchDate() != null).
                    filter(tm -> tm.getId().equals(match.getId())).
                    findFirst().orElse(null);

            if (found == null) {
                assertNotNull(found);
            }
            assertNotNull(found);
        }
    }

    @Override
    public List<TeamMatch> getByTeam(Team team) {
        return teamMatchDao.getByTeam(team);
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
        return teamMatchDao.get(id);
    }
}
