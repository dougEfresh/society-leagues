package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.ApiFactory;

import com.society.leagues.client.api.admin.TeamAdminApi;
import com.society.leagues.client.api.domain.Role;
import com.society.leagues.client.api.domain.Team;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class,TestBase.class})
@SuppressWarnings("deprecated")
public class TeamTest extends TestBase {
    TeamAdminApi api;

    @Before
    public void setup() throws Exception {
        super.setup();
        api = ApiFactory.createApi(TeamAdminApi.class, authenticate(Role.ADMIN), baseURL);
    }

    @Test
    public void testCreate() {
        Team team = new Team(UUID.randomUUID().toString());
        Team returned = api.create(team);
        assertNotNull(returned);
        assertNotNull(returned.getId());
        assertNotNull(returned.getName());

        returned.setName(null);
        assertNull(api.create(returned));

    }

    @Test
    public void testDelete() {
        Team team = new Team(UUID.randomUUID().toString());
        Team returned = api.create(team);
        assertNotNull(returned);
        assertTrue(api.delete(returned));
        assertFalse(api.delete(returned));
    }
}
