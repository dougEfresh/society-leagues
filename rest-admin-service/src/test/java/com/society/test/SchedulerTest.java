package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.domain.Role;
import com.society.leagues.client.api.admin.*;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.client.api.domain.division.DivisionType;
import com.society.leagues.client.api.domain.division.LeagueType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@IntegrationTest(value = {"server.port:0","daemon:true","debug:true"})
public class SchedulerTest extends TestBase implements SchedulerAdminApi {
    SchedulerAdminApi api;
    SeasonAdminApi seasonApi;
    DivisionAdminApi divisionApi;
    TeamAdminApi teamApi;
    private static Logger logger = LoggerFactory.getLogger(SchedulerTest.class);
    
    
    @Before
    public void setup() throws Exception {
        super.setup();
        String token = authenticate(Role.ADMIN);
        api = ApiFactory.createApi(SchedulerAdminApi.class, token, baseURL,true);
        divisionApi = ApiFactory.createApi(DivisionAdminApi.class, token, baseURL);
        seasonApi = ApiFactory.createApi(SeasonAdminApi.class, token, baseURL);
        teamApi = ApiFactory.createApi(TeamAdminApi.class, token, baseURL);

    }

    @Test
    public void testCreate() {
        Division division = new Division(DivisionType.EIGHT_BALL_THURSDAYS,LeagueType.TEAM);
        division = divisionApi.create(division);
        assertNotNull(division);

        Season season = new Season("Whatever", new Date(),20);
        season = seasonApi.create(season);
        assertNotNull(season);

        List<Team> teams = new ArrayList<>();
        for (String teamName: new String[] {"1","2","3","4","5","6","7","8","9","10"}) {
            Team team = new Team("Team " + teamName + " "  +
                    UUID.randomUUID().toString().substring(0,7),
                    division);
            team  = teamApi.create(team);
            assertNotNull(team);
            teams.add(team);
        }
        List<Match> matches = api.create(season.getId(),teams);
        assertNotNull(matches);
        assertFalse(matches.isEmpty());
        
        StringBuilder sb = new StringBuilder(512);
        LocalDate curDate = LocalDate.now();
        sb.append("\n").append(curDate);
        sb.append("--------------------\n");
        int team1Count = 0;
        int team1Home = 0;
        int team1away = 0;
        for (Match match : matches) {
            if (match.getAway().getName().contains("Team 1 ")) {
                team1away++;
                team1Count++;
            }

            if (match.getHome().getName().contains("Team 1 ")) {
                team1Home++;
                team1Count++;
            }
            LocalDate mDate = LocalDate.of(
                    match.getMatchDate().getYear(),
                    match.getMatchDate().getMonth()+1,
                    match.getMatchDate().getDate());
            if (!curDate.isEqual(mDate)) {
                curDate = mDate;
                sb.append("--------------------\n");
                sb.append(curDate);
                sb.append("\n--------------------\n");
            }
            sb.append(String.format("%s\t\t%s",match.getHome().getName(),match.getAway().getName())).append("\n");
        }
        //logger.info(sb.toString());
        assertTrue(team1Count == 20);
        assertTrue(team1Home >= 9);
        assertTrue(team1away >= 9);
        
    }

    @Override
    public List<Match> create(Integer id, List<Team> teams) {
        return null;
    }

    @Test
    public void testCreateMatch() throws Exception {
        Division division = new Division(DivisionType.EIGHT_BALL_THURSDAYS,LeagueType.INDIVIDUAL);
        division = divisionApi.create(division);
        assertNotNull(division);

        Season season = new Season("Whatever", new Date(),20);
        season = seasonApi.create(season);
        assertNotNull(season);
        
        List<Team> teams = new ArrayList<>();
        for (String teamName: new String[] {"1","2","3","4"}) {
            Team team = new Team("Team " + teamName + " "  +
                    UUID.randomUUID().toString().substring(0,7),
                    division);
            team  = teamApi.create(team);
            assertNotNull(team);
            teams.add(team);
        }
        
        Match match = new Match(teams.get(0),teams.get(1),season,new Date());
        match = create(match);
        assertNotNull(match);
        assertNotNull(match.getId());
    }

    @Override
    public Match create(Match match) {
        return api.create(match);
    }
}
