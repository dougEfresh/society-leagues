package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.DivisionApi;
import com.society.leagues.client.api.domain.Role;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.client.api.domain.division.DivisionType;
import com.society.leagues.client.api.domain.division.LeagueType;
import com.society.leagues.client.exception.Unauthorized;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.ProcessingException;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@IntegrationTest(value = {"server.port:0","daemon:true","debug:true"})
public class DivisionTest extends TestBase {
    DivisionApi api;

    @Before
    public void setup() throws Exception {
        super.setup();
        api = ApiFactory.createApi(DivisionApi.class, authenticate(Role.ADMIN), baseURL);
    }

    @Test
    public void testCreate() {
        Division division = new Division(DivisionType.EIGHT_BALL_THURSDAYS, LeagueType.INDIVIDUAL);
        Division returned = api.create(division);
        assertNotNull(returned);
        assertEquals(division.getType(), returned.getType());
        assertNotNull(returned.getId());

        division.setType(null);
        assertNull(api.create(division));
    }

    @Test
    public void testDelete() {
        Division division = new Division(DivisionType.EIGHT_BALL_WEDNESDAYS, LeagueType.INDIVIDUAL);
        Division returned = api.create(division);
        assertTrue(api.delete(returned));
        assertFalse(api.delete(returned));
        returned = api.create(division);
        returned.setId(null);
        assertFalse(api.delete(returned));
    }

    @Test
    public void testModify() {
        Division division = new Division(DivisionType.NINE_BALL_TUESDAYS, LeagueType.TEAM);
        Division returned = api.create(division);

        returned.setType(DivisionType.NINE_BALL_CHALLENGE);

        Division modified = api.modify(returned);
        assertNotNull(modified);
        assertEquals(modified.getType(), DivisionType.NINE_BALL_CHALLENGE);

        returned.setId(null);
        assertNull(api.modify(returned));
    }

    @Test
    public void testNoAccess() {
        try {
            api = ApiFactory.createApi(DivisionApi.class, baseURL);
            api.create(new Division());
        } catch (ProcessingException exception) {
            assertTrue(exception.getCause() instanceof Unauthorized);
        }
    }

}
