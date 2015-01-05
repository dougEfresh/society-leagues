package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.admin.TeamAdminApi;
import com.society.leagues.client.api.Role;
import com.society.leagues.client.api.domain.Team;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class, TestConfig.class, AdminTestConfig.class})
@IntegrationTest(value = {"server.port:0","daemon:true","debug:true"})
public class TeamAdminTest extends TestBase {
    TeamAdminApi api;

    @Before
    public void setup() throws Exception {
        super.setup();
        api = ApiFactory.createApi(TeamAdminApi.class, authenticate(Role.ADMIN), baseURL);
    }

    @Test
    public void testCreate() {
        Team team = new Team(UUID.randomUUID().toString());
        team.setId(1000);
        Mockito.when(mockTeamDao.create(Mockito.any(Team.class))).thenReturn(team);

        Team returned = api.create(team);
        assertNotNull(returned);
        assertEquals(team.getId(), returned.getId());
        assertEquals(team.getName(),returned.getName());

        Mockito.reset(mockTeamDao);
        returned.setName(null);
        assertNull(api.create(returned));

    }

    @Test
    public void testDelete() {
        Team team = new Team(UUID.randomUUID().toString());
        team.setId(1001);
        Mockito.when(mockTeamDao.delete(Mockito.any(Team.class))).thenReturn(Boolean.TRUE);

        assertTrue(api.delete(team));

        Mockito.reset(mockTeamDao);
        Mockito.when(mockTeamDao.delete(Mockito.any(Team.class))).thenReturn(Boolean.FALSE);
        assertFalse(api.delete(team));
    }
}
