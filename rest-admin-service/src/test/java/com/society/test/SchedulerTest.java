package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.Role;
import com.society.leagues.client.api.admin.*;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.client.api.domain.division.DivisionType;
import com.society.leagues.client.api.domain.league.League;
import com.society.leagues.client.api.domain.league.LeagueType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@IntegrationTest(value = {"server.port:0","daemon:true","debug:true"})
public class SchedulerTest extends TestBase {
    SchedulerAdminApi api;
    SeasonAdminApi seasonApi;
    LeagueAdminApi leagueApi;
    DivisionAdminApi divisionApi;
    TeamAdminApi teamApi;

    @Before
    public void setup() throws Exception {
        super.setup();
        String token = authenticate(Role.ADMIN);
        api = ApiFactory.createApi(SchedulerAdminApi.class, token, baseURL,true);
        leagueApi = ApiFactory.createApi(LeagueAdminApi.class, token, baseURL);
        divisionApi = ApiFactory.createApi(DivisionAdminApi.class, token, baseURL);
        seasonApi = ApiFactory.createApi(SeasonAdminApi.class, token, baseURL);
        teamApi = ApiFactory.createApi(TeamAdminApi.class, token, baseURL);

    }

    @Test
    public void testCreate() {
        League league = new League(LeagueType.TEAM);
        league = leagueApi.create(league);
        assertNotNull(league);

        Division division = new Division(DivisionType.EIGHT_BALL_THURSDAYS,league);
        division = divisionApi.create(division);
        assertNotNull(division);

        Season season = new Season(division,"Whatever", LocalDate.now());
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
    }
}
