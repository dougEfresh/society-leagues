package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.admin.api.TeamAdminApi;
import com.society.leagues.client.api.Role;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.league.League;
import com.society.leagues.client.api.domain.league.LeagueType;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@IntegrationTest(value = {"server.port:0","daemon:true","debug:true"})
public class TeamIntegrationTest extends TestIntegrationBase {
    TeamAdminApi api;

    @Before
    public void setup() throws Exception {
        super.setup();
        api = ApiFactory.createApi(TeamAdminApi.class, authenticate(Role.ADMIN), baseURL);
    }

    @Test
    public void testCreate() {
        League league = new League(LeagueType.INDIVIDUAL,0d,1);
        Team team = new Team("TheBestEva",league,2);

        Team returned = api.create(team);
        assertNotNull(returned);

        assertNotNull(returned.getId());
        assertEquals(team.getName(),returned.getName());
        assertNotNull(returned.getLeague());
        assertEquals(team.getLeague().getId(), returned.getLeague().getId());

        returned.setName(null);
        assertNull(api.create(returned));

        returned.setName("Blah");
        returned.setLeague(null);
        assertNull(api.create(returned));

        league.setId(null);
        returned.setLeague(league);
        assertNull(api.create(returned));

    }

    @Test
    public void testModify() {
        League league = new League(LeagueType.INDIVIDUAL,0d,1);
        Team team = new Team("TheBestEva",league);
        Team created = api.create(team);

        league.setId(123);
        created.setLeague(league);
        Team returned = api.modify(created);
        assertNotNull(returned);
        assertEquals(created.getId(),returned.getId());
        assertEquals(created.getName(),returned.getName());
        assertNotNull(returned.getLeague());
        assertEquals(team.getLeague().getId(),returned.getLeague().getId());

        league.setId(null);
        returned.setLeague(league);
        assertNull(api.modify(returned));
    }
}
