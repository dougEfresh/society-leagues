package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.admin.DivisionAdminApi;
import com.society.leagues.client.api.admin.LeagueAdminApi;
import com.society.leagues.client.api.Role;
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

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@IntegrationTest(value = {"server.port:0","daemon:true","debug:true"})
public class DivisionIntegrationTest extends TestIntegrationBase {
    DivisionAdminApi api;
    LeagueAdminApi leagueApi;

    @Before
    public void setup() throws Exception {
        super.setup();
        api = ApiFactory.createApi(DivisionAdminApi.class, authenticate(Role.ADMIN), baseURL);
        leagueApi = ApiFactory.createApi(LeagueAdminApi.class, authenticate(Role.ADMIN), baseURL);
    }

    @Test
    public void testCreate() {
        League league = new League(LeagueType.INDIVIDUAL);
        league = leagueApi.create(league);
        Division division = new Division(DivisionType.EIGHT_BALL_THURSDAYS,league);
        Division returned = api.create(division);
        assertNotNull(returned);
        assertEquals(division.getType(),returned.getType());
        assertNotNull(returned.getId());

        division.setType(null);
        assertNull(api.create(division));
    }

    @Test
    public void testDelete() {
        League league = new League(LeagueType.MIXED);
        league = leagueApi.create(league);
        Division division = new Division(DivisionType.EIGHT_BALL_WEDNESDAYS,league);
        Division returned = api.create(division);
        assertTrue(api.delete(returned));
        assertFalse(api.delete(returned));
    }

    @Test
    public void testModify() {
        League league = new League(LeagueType.TEAM);
        league = leagueApi.create(league);
        Division division = new Division(DivisionType.NINE_BALL_TUESDAYS,league);
        Division returned = api.create(division);

        returned.setType(DivisionType.INDIVIDUAL_CHALLENGE);

        Division modified = api.modify(returned);
        assertNotNull(modified);
        assertEquals(modified.getType(),DivisionType.INDIVIDUAL_CHALLENGE);

        returned.setId(null);
        assertNull(api.modify(returned));
    }
}
