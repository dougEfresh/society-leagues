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
import com.society.leagues.client.exception.Unauthorized;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class, TestConfig.class, AdminTestConfig.class})
@IntegrationTest(value = {"server.port:0","daemon:true","debug:true"})
public class SeasonAdminTest  extends TestBase {
    LeagueAdminApi leagueApi;
    DivisionAdminApi divisionApi;
    SeasonAdminApi api;

    @Before
    public void setup() throws Exception {
        super.setup();
        String token = authenticate(Role.ADMIN);
        leagueApi = ApiFactory.createApi(LeagueAdminApi.class, token, baseURL);
        divisionApi = ApiFactory.createApi(DivisionAdminApi.class, token, baseURL);
        api = ApiFactory.createApi(SeasonAdminApi.class, token, baseURL);
    }


    @Test
    public void testCreate() {
        League league = new League(LeagueType.INDIVIDUAL);
        league.setId(3000);
        Division division = new Division(DivisionType.EIGHT_BALL_THURSDAYS,league);
        division.setId(4000);
        Season season = new Season(division,"Cool",new Date());
        season.setId(5001);
        Mockito.when(mockSeasonDao.create(Mockito.any(Season.class))).thenReturn(season);
        Season returned = api.create(season);
        assertNotNull(returned);
        assertEquals(season.getName(),returned.getName());
        assertEquals(season.getId(),returned.getId());

        Mockito.reset(mockSeasonDao);

        division.setId(null);
        assertNull(api.create(season));
    }

    @Test
    public void testDelete() {
        League league = new League(LeagueType.MIXED);
        league.setId(3001);
        Division division = new Division(DivisionType.NINE_BALL_TUESDAYS,league);
        division.setId(4001);
        Season season = new Season(division,"Cool",new Date());
        season.setId(5001);
        Mockito.when(mockSeasonDao.delete(Mockito.any(Season.class))).thenReturn(Boolean.TRUE);
        assertTrue(api.delete(season));

        Mockito.reset(mockSeasonDao);
        Mockito.when(mockSeasonDao.delete(Mockito.any(Season.class))).thenReturn(Boolean.FALSE);
        assertFalse(api.delete(season));
    }

    @Test
    public void testModify() {
        League league = new League(LeagueType.TEAM);
        league.setId(3002);
        Division division = new Division(DivisionType.MIXED_MONDAYS,league);
        division.setId(4002);
        Season season = new Season(division,"Cool",new Date());
        season.setId(5002);
        Mockito.when(mockSeasonDao.modify(Mockito.any(Season.class))).thenReturn(season);
        assertNotNull(api.modify(season));

        season.setId(null);
        assertNull(api.modify(season));
    }

    @Test
    public void testNoAccess() {
        api = ApiFactory.createApi(SeasonAdminApi.class,authenticate(Role.Player),baseURL);
        try {
            api.create(new Season(new Division(),"No",new Date()));
        } catch (Throwable t){
            assertTrue(t.getCause() instanceof Unauthorized);
        }
    }

}
