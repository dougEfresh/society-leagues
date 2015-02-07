package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.domain.Role;
import com.society.leagues.client.api.UserClientApi;
import com.society.leagues.client.api.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class,TestBase.class})
public class UserClientTest extends TestClientBase {
    UserClientApi api;

    @Before
    public void setup() throws Exception {
        super.setup();
        api = ApiFactory.createApi(UserClientApi.class, authenticate(Role.ADMIN), baseURL);
    }

    @Test
    public void testCurrent() throws Exception {
        List<User> users = api.get();
        assertNotNull(users);
        assertFalse(users.isEmpty());
    }

    @Test
    public void testPlayers() throws Exception {
        List<User> users = api.get();
        assertNotNull(users);
        assertFalse(users.isEmpty());
        users = users.stream().filter(u -> u.getLogin().contains("login")).collect(Collectors.toList());
        assertNotNull(users);
        assertFalse(users.isEmpty());

        for (User user : users) {
            assertFalse(user.getPlayers().isEmpty());
            assertFalse(user.getTeams().isEmpty());
            assertFalse(user.getSeasons().isEmpty());
            assertFalse(user.getDivisions().isEmpty());
        }
    }
}
