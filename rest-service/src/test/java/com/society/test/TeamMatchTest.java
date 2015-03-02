package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.api.admin.*;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.client.api.domain.division.DivisionType;
import com.society.leagues.dao.*;
import com.society.leagues.dao.DivisionDao;
import com.society.leagues.dao.SeasonDao;
import com.society.leagues.dao.TeamDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class,TestBase.class})
public class TeamMatchTest extends TestBase implements MatchAdminApi {
    @Autowired MatchDao api;
    @Autowired SeasonDao seasonApi;
    @Autowired DivisionDao divisionApi;
    @Autowired TeamDao teamApi;

    @Before
    public void setup() throws Exception {
        super.setup();
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
        teamMatch = api.create(teamMatch);
        assertNotNull(teamMatch);
        assertNotNull(teamMatch.getId());
    }

    @Override
    public TeamMatch create(TeamMatch teamMatch) {
        return api.create(teamMatch);
    }

    @Test
    public void testModify() throws Exception {
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
        teamMatch = api.create(teamMatch);
        assertNotNull(teamMatch);
        assertNotNull(teamMatch.getId());

        teamMatch.setHome(away);

        teamMatch = api.modify(teamMatch);
        assertNotNull(teamMatch);

        assertEquals(teamMatch.getHome().getId(), teamMatch.getAway().getId());
    }

    @Override
    public TeamMatch modify(TeamMatch teamMatch) {
        return api.modify(teamMatch);
    }
}
