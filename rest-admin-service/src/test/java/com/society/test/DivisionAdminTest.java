package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.admin.DivisionAdminApi;
import com.society.leagues.client.api.Role;
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

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class, TestConfig.class, AdminTestConfig.class})
@IntegrationTest(value = {"server.port:0","daemon:true","debug:true"})
public class DivisionAdminTest extends TestBase {
    DivisionAdminApi api;

    @Before
    public void setup() throws Exception {
        super.setup();
        api = ApiFactory.createApi(DivisionAdminApi.class, authenticate(Role.ADMIN), baseURL);
    }

    @Test
    public void testCreate() {
        League league = new League(LeagueType.INDIVIDUAL);
        league.setId(3000);
        Division division = new Division(DivisionType.EIGHT_BALL_THURSDAYS,league);
        division.setId(4000);
        Mockito.when(mockDivisionDao.create(Mockito.any(Division.class))).thenReturn(division);
        Division returned = api.create(division);
        assertNotNull(returned);
        assertEquals(division.getType(),returned.getType());
        assertEquals(division.getId(),returned.getId());

        Mockito.reset(mockDivisionDao);

        league.setId(null);
        assertNull(api.create(division));
    }

    @Test
    public void testDelete() {
        League league = new League(LeagueType.MIXED);
        league.setId(3001);
        Division division = new Division(DivisionType.NINE_BALL_TUESDAYS,league);
        division.setId(4001);
        Mockito.when(mockDivisionDao.delete(Mockito.any(Division.class))).thenReturn(Boolean.TRUE);
        assertTrue(api.delete(division));

        Mockito.reset(mockDivisionDao);
        Mockito.when(mockDivisionDao.delete(Mockito.any(Division.class))).thenReturn(Boolean.FALSE);
        assertFalse(api.delete(division));
    }

    @Test
    public void testModify() {
        League league = new League(LeagueType.TEAM);
        league.setId(3002);
        Division division = new Division(DivisionType.MIXED_MONDAYS,league);
        division.setId(4002);

        Mockito.when(mockDivisionDao.modify(Mockito.any(Division.class))).thenReturn(division);
        assertNotNull(api.modify(division));

        division.setId(null);
        assertNull(api.modify(division));
    }

    @Test
    public void testNoAccess() {
        api = ApiFactory.createApi(DivisionAdminApi.class,authenticate(Role.Player),baseURL);
        try {
            api.create(new Division(DivisionType.INDIVIDUAL_CHALLENGE,new League()));
        } catch (Throwable t){
            assertTrue(t.getCause() instanceof Unauthorized);
        }
    }
}
