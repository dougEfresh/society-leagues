package com.society.test.client;

import com.society.leagues.client.api.domain.Team;
import com.society.leagues.dao.TeamDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class TeamClientTest extends TestClientBase {
    @Autowired TeamDao api;

    /*
    @Before
    public void setup() throws Exception {
        super.setup();

        api = ApiFactory.createApi(TeamClientApi.class, authenticate(Role.ADMIN), baseURL);
    }
    */
    
    @Test
    public void testListing() {
        List<Team> teams = api.get();
        assertNotNull(teams);
        assertFalse(teams.isEmpty());
    }
}
