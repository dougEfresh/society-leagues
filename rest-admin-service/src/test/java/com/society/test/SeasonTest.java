package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.Role;
import com.society.leagues.client.api.admin.DivisionAdminApi;
import com.society.leagues.client.api.admin.LeagueAdminApi;
import com.society.leagues.client.api.admin.SeasonAdminApi;
import com.society.leagues.client.api.domain.Season;
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

import java.util.Date;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@IntegrationTest(value = {"server.port:0","daemon:true","debug:true"})
public class SeasonTest extends TestBase {
    LeagueAdminApi leagueApi;
    DivisionAdminApi divisionApi;
    SeasonAdminApi api;

    @Before
    public void setup() throws Exception {
        super.setup();
        String token = authenticate(Role.ADMIN);
        leagueApi = ApiFactory.createApi(LeagueAdminApi.class, token, baseURL,true);
        divisionApi = ApiFactory.createApi(DivisionAdminApi.class, token, baseURL, true);
        api = ApiFactory.createApi(SeasonAdminApi.class, token, baseURL,true);
    }

    @Test
    public void testCreate() {
        League league = new League(LeagueType.INDIVIDUAL);
        league = leagueApi.create(league);

        Division division = new Division(DivisionType.EIGHT_BALL_THURSDAYS,league);
        division = divisionApi.create(division);

        Season season = new Season(division,"Cool", new Date(),10);

        Season returned = api.create(season);
        assertNotNull(returned);
        assertEquals(season.getName(),returned.getName());
        assertNotNull(returned.getId());

        division.setId(null);
        assertNull(api.create(season));
    }

    @Test
    public void testDelete() {
        League league = new League(LeagueType.MIXED);
        league = leagueApi.create(league);

        Division division = new Division(DivisionType.NINE_BALL_TUESDAYS,league);
        division = divisionApi.create(division);

        Season season = new Season(division,"9Ball",new Date(),10);

        season = api.create(season);
        assertTrue(api.delete(season));
        assertFalse(api.delete(season));
    }

    @Test
    public void testModify() {
        League league = new League(LeagueType.MIXED);
        league = leagueApi.create(league);

        Division division = new Division(DivisionType.NINE_BALL_TUESDAYS,league);
        division = divisionApi.create(division);

        Season season = new Season(division,"ChangeMe",new Date(),10);

        season = api.create(season);
        season.setName("Blah");
        assertNotNull(api.modify(season));

        season.setId(null);
        assertNull(api.modify(season));
    }
}
