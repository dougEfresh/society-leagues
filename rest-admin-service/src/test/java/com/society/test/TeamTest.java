package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.admin.DivisionAdminApi;
import com.society.leagues.client.api.admin.SeasonAdminApi;
import com.society.leagues.client.api.admin.TeamAdminApi;
import com.society.leagues.client.api.Role;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.client.api.domain.division.DivisionType;
import com.society.leagues.client.api.domain.division.LeagueType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@IntegrationTest(value = {"server.port:0","daemon:true","debug:true"})
@SuppressWarnings("deprecated")
public class TeamTest extends TestBase {
    DivisionAdminApi divisionApi;
    SeasonAdminApi seasonApi;
    TeamAdminApi api;

    @Before
    public void setup() throws Exception {
        super.setup();
        String token = authenticate(Role.ADMIN);
        divisionApi = ApiFactory.createApi(DivisionAdminApi.class, token, baseURL);
        seasonApi = ApiFactory.createApi(SeasonAdminApi.class, token, baseURL);
        api = ApiFactory.createApi(TeamAdminApi.class, authenticate(Role.ADMIN), baseURL);
    }

    @Test
    public void testCreate() {
        Division division = new Division(DivisionType.NINE_BALL_TUESDAYS,LeagueType.TEAM);
        division = divisionApi.create(division);

        Team team = new Team(UUID.randomUUID().toString(),division);
        Team returned = api.create(team);
        assertNotNull(returned);
        assertNotNull(returned.getId());
        assertNotNull(returned.getName());
        assertNotNull(returned.getDivision());
        assertNotNull(returned.getDivision().getId());

        returned.setName(null);
        assertNull(api.create(returned));

    }

    @Test
    public void testDelete() {
        Division division = new Division(DivisionType.NINE_BALL_TUESDAYS,LeagueType.INDIVIDUAL);
        division = divisionApi.create(division);

        Team team = new Team(UUID.randomUUID().toString(),division);
        Team returned = api.create(team);
        assertNotNull(returned);
        assertTrue(api.delete(returned));
        assertFalse(api.delete(returned));
    }
}
