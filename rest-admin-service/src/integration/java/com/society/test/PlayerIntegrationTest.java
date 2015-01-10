package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.admin.DivisionAdminApi;
import com.society.leagues.client.api.admin.LeagueAdminApi;
import com.society.leagues.client.api.admin.PlayerAdminApi;
import com.society.leagues.client.api.Role;
import com.society.leagues.client.api.admin.SeasonAdminApi;
import com.society.leagues.client.api.domain.Player;
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
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@IntegrationTest(value = {"server.port:0","daemon:true","debug:true"})
public class PlayerIntegrationTest extends TestIntegrationBase {
    PlayerAdminApi api;
    SeasonAdminApi seasonApi;
    LeagueAdminApi leagueApi;
    DivisionAdminApi divisionApi;


    @Before
    public void setup() throws Exception {
        super.setup();
        String token = authenticate(Role.ADMIN);
        api = ApiFactory.createApi(PlayerAdminApi.class, token, baseURL);
        leagueApi = ApiFactory.createApi(LeagueAdminApi.class, token, baseURL);
        divisionApi = ApiFactory.createApi(DivisionAdminApi.class, token, baseURL);
        seasonApi = ApiFactory.createApi(SeasonAdminApi.class, token, baseURL);
    }

    @Test
    public void testCreate() {
        League league = new League(LeagueType.INDIVIDUAL);
        league.setId(3000);
        Division division = new Division(DivisionType.EIGHT_BALL_THURSDAYS,league);
        division.setId(4000);
        Season season = new Season(division,"Cool",new Date());

    }
}
