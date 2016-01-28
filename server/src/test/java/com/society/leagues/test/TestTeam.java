package com.society.leagues.test;

import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.TeamMembers;
import com.society.leagues.client.api.domain.User;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;


public class TestTeam  extends BaseTest {

    private static Logger logger = Logger.getLogger(TestUser.class);

    @Test
    public void testTeam() {
        assertFalse(teamApi.active().isEmpty());
        assertTrue(teamApi.active().stream().filter(t->!t.getSeason().isActive()).count() == 0);
        User u = userApi.active().get(20);
        List<Team> userTeams = teamApi.userTeams(u.getId());
        assertNotNull(userTeams);
        assertFalse(userTeams.isEmpty());
        assertTrue(userTeams.stream().filter(t->!t.hasUser(u)).count() == 0);
        for (Season season : seasonApi.active()) {
            assertFalse(teamApi.seasonTeams(season.getId()).isEmpty());
        }
        assertEquals(userTeams.iterator().next(),teamApi.get(userTeams.iterator().next().getId()));
    }

    @Test
    public void testCreate() {
        Season season = seasonApi.active().stream().filter(s->!s.isChallenge()).findAny().get();
        Team team = new Team();
        team.setName(UUID.randomUUID().toString());
        team.setSeason(season);
        Team newTeam = teamApi.save(team);
        assertEquals(team.getName(),newTeam.getName());
        assertNotNull(newTeam.getId());
        assertEquals(season,newTeam.getSeason());

        User u = userApi.active().stream().filter(user->!user.isChallenge()).findAny().get();
        TeamMembers tm = new TeamMembers(Arrays.asList(u));
        TeamMembers newTm = teamApi.saveMembers(newTeam.getId(),tm);
        assertNotNull(newTm.getId());
        newTm = teamApi.members(newTeam.getId());
        assertNotNull(newTm.getId());
        assertTrue(newTm.getMembers().contains(u));
        assertTrue(newTm.getMembers().size() == 1);
        teamApi.delete(newTeam.getId());
    }


    @Test
    public void testTeamModify() {
        Team team = teamApi.active().stream().filter(t->!t.getSeason().isChallenge()).findAny().get();
        team.setName(UUID.randomUUID().toString());
        teamApi.save(team);
        Team newTeam = teamApi.get(team.getId());
        assertEquals(team,newTeam);
        assertEquals(team.getName(),newTeam.getName());
    }

}
