package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.admin.LeagueAdminApi;
import com.society.leagues.client.api.Role;
import com.society.leagues.client.api.domain.league.League;
import com.society.leagues.client.api.domain.league.LeagueType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@IntegrationTest(value = {"server.port:0","daemon:true","debug:true"})
public class LeagueIntegrationTest extends TestIntegrationBase {
    LeagueAdminApi api;

    @Before
    public void setup() throws Exception {
        super.setup();
        api = ApiFactory.createApi(LeagueAdminApi.class, authenticate(Role.ADMIN), baseURL);
    }

    @Test
    public void testCreate() {
        League league = new League(LeagueType.INDIVIDUAL);
        League returned = api.create(league);
        assertNotNull(returned);
        assertEquals(league.getType(),returned.getType());
        assertNotNull(returned.getId());

        league.setType(null);
        assertNull(api.create(league));
    }

    @Test
    public void testDelete() {
        League league = new League(LeagueType.MIXED);
        League returned = api.create(league);
        assertTrue(api.delete(returned));
        assertFalse(api.delete(returned));
    }

    @Test
    public void testModify() {
        League league = new League(LeagueType.TEAM);
        League returned = api.create(league);
        returned.setType(LeagueType.INDIVIDUAL);

        League modified = api.modify(returned);
        assertNotNull(modified);
        assertEquals(modified.getType(),LeagueType.INDIVIDUAL);

        league.setId(null);
        assertNull(api.modify(league));

        league.setId(-100);
        assertNull(api.modify(league));
    }

}
