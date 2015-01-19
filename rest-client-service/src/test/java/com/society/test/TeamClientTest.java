package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.Role;
import com.society.leagues.client.api.TeamClientApi;
import com.society.leagues.client.api.domain.Team;
import jersey.repackaged.com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@IntegrationTest(value = {"server.port:0","daemon:true","debug:true"})
public class TeamClientTest extends TestBase {
    TeamClientApi api;
    @Autowired JdbcTemplate jdbcTemplate;
    
    @Before
    public void setup() throws Exception {
        super.setup();
        generateData();
        api = ApiFactory.createApi(TeamClientApi.class, authenticate(Role.ADMIN), baseURL);
    }
    
    @Test
    public void testListing() {
        List<Team> teams = api.all(SchemaData.users.get(0).getId());
        assertNotNull(teams);
        assertFalse(teams.isEmpty());

        teams = api.current(SchemaData.users.get(0).getId());
        assertNotNull(teams);
        assertFalse(teams.isEmpty());

        Team team = api.get(teams.get(0).getId());
        assertNotNull(team);
        teams = api.past(Lists.newArrayList(SchemaData.users));
        
        assertNotNull(teams);
        assertTrue(teams.isEmpty());
        
    }
}
