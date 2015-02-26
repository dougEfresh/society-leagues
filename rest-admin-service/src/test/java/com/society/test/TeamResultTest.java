package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.AuthApi;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.TeamResult;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.client.api.domain.division.DivisionType;
import com.society.leagues.dao.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true, value = {"embedded=true", "generate=false"})
public class TeamResultTest {

    @Autowired TeamResultDao api;
    AuthApi authApi;
    @Autowired MatchDao matchApi;
    @Autowired SeasonDao seasonApi;
    @Autowired DivisionDao divisionApi;
    @Autowired TeamDao teamApi;
    @Autowired ApiFactory apiFactory;
    @Value("${local.server.port}")
    public int port;

    @Before
    public void setup() throws Exception {
        /*
        authApi = apiFactory.getApi(AuthApi.class,"http://localhost:" + port,true);
        authApi.login(Schema.ADMIN_USER,Schema.ADMIN_PASS);
        api = apiFactory.getApi(TeamResultAdminApi.class,"http://localhost:" + port,true);
        */
    }

    @Test
    public void testCreate() {
        Division division = new Division(DivisionType.EIGHT_BALL_THURSDAYS);
        division = divisionApi.create(division);
        assertNotNull(division);

        Season season = new Season(UUID.randomUUID().toString(),new Date(),20);
        season = seasonApi.create(season);
        assertNotNull(season);
        Team home = new Team(UUID.randomUUID().toString());
        Team away = new Team(UUID.randomUUID().toString());
        home = teamApi.create(home);
        assertNotNull(home);
        away = teamApi.create(away);
        assertNotNull(away);

        TeamMatch teamMatch = new TeamMatch();
        teamMatch.setDivision(division);
        teamMatch.setSeason(season);
        teamMatch.setHome(home);
        teamMatch.setAway(away);
        teamMatch.setMatchDate(new Date());
        teamMatch = matchApi.create(teamMatch);
        assertNotNull(teamMatch);
        assertNotNull(teamMatch.getId());

        TeamResult result = new TeamResult();
        result.setTeamMatch(teamMatch);
        result.setHomeRacks(5);
        result.setAwayRacks(4);

        result = api.create(result);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertTrue(result.getHomeRacks() == 5);
        assertTrue(result.getAwayRacks() == 4);
    }

    @Test
    public void testModify() {
        Division division = new Division(DivisionType.EIGHT_BALL_THURSDAYS);
        division = divisionApi.create(division);
        assertNotNull(division);

        Season season = new Season(UUID.randomUUID().toString(),new Date(),20);
        season = seasonApi.create(season);
        assertNotNull(season);
        Team home = new Team(UUID.randomUUID().toString());
        Team away = new Team(UUID.randomUUID().toString());
        home = teamApi.create(home);
        assertNotNull(home);
        away = teamApi.create(away);
        assertNotNull(away);

        TeamMatch teamMatch = new TeamMatch();
        teamMatch.setDivision(division);
        teamMatch.setSeason(season);
        teamMatch.setHome(home);
        teamMatch.setAway(away);
        teamMatch.setMatchDate(new Date());
        teamMatch = matchApi.create(teamMatch);
        assertNotNull(teamMatch);
        assertNotNull(teamMatch.getId());

        TeamResult result = new TeamResult();
        result.setTeamMatch(teamMatch);
        result.setHomeRacks(5);
        result.setAwayRacks(4);

        result = api.create(result);
        assertNotNull(result);
        assertNotNull(result.getId());

        result.setHomeRacks(10);
        result.setAwayRacks(0);
        result = api.modify(result);
        assertNotNull(result);
        assertTrue(result.getHomeRacks() == 10);
        assertTrue(result.getAwayRacks() == 0);
    }
}
