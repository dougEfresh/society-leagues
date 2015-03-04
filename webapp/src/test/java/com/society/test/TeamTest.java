package com.society.test;

import com.society.leagues.client.api.domain.Team;
import com.society.leagues.dao.TeamDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class TeamTest extends TestBase {
    @Autowired TeamDao api;

    @Before
    public void setup() throws Exception {
        super.setup();
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
