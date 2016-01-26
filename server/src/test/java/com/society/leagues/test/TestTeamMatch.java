package com.society.leagues.test;

import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.TeamMatch;
import feign.RetryableException;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;


public class TestTeamMatch extends BaseTest {

    @Test
    public void testCreate() {
        Season season = seasonApi.active().stream().filter(s->!s.isChallenge()).findAny().get();
        List<Team> teams = teamApi.seasonTeams(season.getId());
        TeamMatch tm = new TeamMatch(teams.get(0), teams.get(1), LocalDateTime.now());
        tm.setHomeRacks(1);
        List<TeamMatch> matches = teamMatchApi.save(Collections.singletonList(tm));
        TeamMatch newTm = matches.iterator().next();
        assertEquals(season,newTm.getSeason());
        assertEquals(teams.get(0),newTm.getHome());
        assertEquals(teams.get(1),newTm.getAway());
        assertTrue(newTm.getHomeRacks() == 1);
        assertTrue(newTm.getAwayRacks() == 0);
        assertTrue(newTm.getHomeForfeits() == 0);
        assertTrue(newTm.getAwayForfeits() == 0);
        assertTrue(newTm.getSetAwayWins() == 0);
        assertTrue(newTm.getSetHomeWins() == 0);
        assertTrue(tm.getMatchDate().isEqual(newTm.getMatchDate()));
        List<TeamMatch> homeTeamMatches  = teamMatchApi.getTeamMatchByTeam(teams.get(0).getId());
        assertTrue(homeTeamMatches.stream().filter(m->m.getId().equals(newTm.getId())).count() == 1);
        teamMatchApi.delete(newTm.getId());
        boolean error = false;
        try {
            teamMatchApi.get(newTm.getId());
        } catch (RetryableException r) {
            error = true;
        }
        assertTrue(error);

    }

    @Test
    public void testAdd() {
        Season season = seasonApi.active().stream().filter(s->!s.isChallenge()).findAny().get();
        TeamMatch tm =  teamMatchApi.add(season.getId(), LocalDate.now().toString());
        assertNotNull(tm);
        assertTrue(tm.getMatchDate().toLocalDate().isEqual(LocalDate.now()));

    }

    @Test
    public void testTeamStats() {
        Season season = seasonApi.active().stream().filter(s -> !s.isChallenge()).findAny().get();
        TeamMatch tm = teamMatchApi.add(season.getId(), LocalDate.now().toString());
        Team home = tm.getHome();
        Team away = tm.getAway();
        tm.setHomeRacks(0);
        tm.setAwayRacks(1);
        TeamMatch newTm = teamMatchApi.save(Collections.singletonList(tm)).stream().filter(t -> t.equals(tm)).findAny().get();
        Team h = statApi.teamSeasonStats(season.getId()).stream().filter(st->st.equals(home)).findAny().get();
        Team a = statApi.teamSeasonStats(season.getId()).stream().filter(st->st.equals(away)).findAny().get();
        assertTrue(home.getStats().getLoses() + 1 == h.getStats().getLoses());
       // assertFalse(home.getStats().getRank().equals(newTm.getHome().getStats().getRank()));
        assertTrue(away.getStats().getWins() + 1 == a.getStats().getWins());

    }

    /*
    @Test
    public void testModify() {
        Team home = utils.createRandomTeam();
        Team away = utils.createRandomTeam();
        TeamMatch tm = leagueService.save(new TeamMatch(home, away, LocalDateTime.now()));
        User u1 = utils.createRandomUser();
        User u2 = utils.createRandomUser();
        home.addMember(u1);
        away.addMember(u2);
        leagueService.save(home);
        leagueService.save(away);
        tm.setAwayRacks(7);
        tm.setHomeRacks(6);
        HttpEntity requestEntity = new HttpEntity(tm, requestHeaders);
        TeamMatch newTeamMatch  =  restTemplate.postForEntity(host + "/api/teammatch/admin/modify", requestEntity, TeamMatch.class).getBody();
        assertNotNull(newTeamMatch.getId());
        assertNotNull(newTeamMatch.getSeason());
        assertNotNull(newTeamMatch.getDivision());
        assertTrue(newTeamMatch.getHome().getMembers().contains(u1));
        assertTrue(newTeamMatch.getAway().getMembers().contains(u2));
        assertEquals(new Integer(7), newTeamMatch.getAwayRacks());
        assertEquals(new Integer(6), newTeamMatch.getHomeRacks());
    }

    @Test
    public void testGet() {
        Team home = utils.createRandomTeam();
        Team away = utils.createRandomTeam();
        TeamMatch tm = leagueService.save(new TeamMatch(home, away, LocalDateTime.now()));
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        TeamMatch newTeamMatch  =  restTemplate.exchange(host + "/api/teammatch/get/" + tm.getId(), HttpMethod.GET, requestEntity, TeamMatch.class).getBody();
        assertEquals(tm.getId(),newTeamMatch.getId());
        assertEquals(tm.getMatchDate(),newTeamMatch.getMatchDate());
        assertEquals(tm.getHome(),newTeamMatch.getHome());
        assertEquals(tm.getAway(),newTeamMatch.getAway());
    }

    @Test
    public void testTeamGetSeason() {
        Team home = utils.createRandomTeam();
        Team away = utils.createRandomTeam();
        User u = utils.createRandomUser();
        home.addMember(u);
        home = leagueService.save(home);
        TeamMatch tm = leagueService.save(new TeamMatch(home, away, LocalDateTime.now()));
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        List<TeamMatch> teams = Arrays.asList(restTemplate.exchange(host + "/api/teammatch/get/user/" + u.getId() + "/current", HttpMethod.GET, requestEntity, TeamMatch[].class).getBody());
        assertNotNull(teams);
        assertTrue(teams.size() > 0);
        for (TeamMatch team : teams) {
            assertTrue(team.hasUser(u));
            assertTrue(team.getSeason().isActive());
        }
    }
    */
}
