package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.SchemaData;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.domain.Role;
import com.society.leagues.client.api.TeamClientApi;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.dao.TeamDao;
import com.society.leagues.dao.UserDao;
import jersey.repackaged.com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true, value = {"embedded=true", "generate=true"})
public class TeamClientTest  {
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
