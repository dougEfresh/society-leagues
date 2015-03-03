package com.society.test;

import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.client.api.domain.division.DivisionType;
import com.society.leagues.dao.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class PlayerResultTest extends TestBase {

    @Autowired PlayerResultDao api;
    @Autowired TeamResultDao teamResultApi;
    @Autowired TeamMatchDao matchApi;
    @Autowired SeasonDao seasonApi;
    @Autowired DivisionDao divisionApi;
    @Autowired TeamDao teamApi;

    @Test
    public void testCreate() throws Exception {
        /*
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
        result.setTeamMatchId(teamMatch.getId());
        result.setHomeRacks(5);
        result.setAwayRacks(4);

        result = teamResultApi.create(result);

        PlayerResult playerResult = new PlayerResult();
     */

    }
}
