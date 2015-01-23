package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.admin.*;
import com.society.leagues.client.api.domain.Role;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.client.api.domain.division.DivisionType;
import com.society.leagues.client.api.domain.division.LeagueType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@IntegrationTest(value = {"server.port:0","daemon:true","debug:true"})
public class PlayerTest extends TestBase {
    PlayerAdminApi api;
    SeasonAdminApi seasonApi;
    DivisionAdminApi divisionApi;
    TeamAdminApi teamApi;
    UserAdminApi userApi;

    @Before
    public void setup() throws Exception {
        super.setup();
        String token = authenticate(Role.ADMIN);
        api = ApiFactory.createApi(PlayerAdminApi.class, token, baseURL);
        divisionApi = ApiFactory.createApi(DivisionAdminApi.class, token, baseURL);
        seasonApi = ApiFactory.createApi(SeasonAdminApi.class, token, baseURL);
        teamApi = ApiFactory.createApi(TeamAdminApi.class, token, baseURL);
        userApi = ApiFactory.createApi(UserAdminApi.class, token, baseURL);
    }

    @Test
    public void testCreate() {
        Division division = new Division(DivisionType.EIGHT_BALL_THURSDAYS,LeagueType.TEAM);
        division = divisionApi.create(division);
        assertNotNull(division);

        Season season = new Season("Cool", new Date(),10);
        season.setSeasonStatus(Status.ACTIVE);
        season = seasonApi.create(season);
        assertNotNull(season);

        Team team = new Team(UUID.randomUUID().toString(),division);
        team  = teamApi.create(team);
        assertNotNull(team);

        User user = new User(UUID.randomUUID().toString(),"password");
        user = userApi.create(user);
        assertNotNull(user);

        Player player = new Player(season,user,team,Handicap.D,division);
        player.setStatus(Status.ACTIVE);
        player = api.create(player);
        assertNotNull(player);
        assertNotNull(player.getId());

    }
}
