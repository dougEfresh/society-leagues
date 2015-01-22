package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.Role;
import com.society.leagues.client.api.UserApi;
import com.society.leagues.client.api.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@IntegrationTest(value = {"server.port:0","daemon:true","debug:true"})
public class UserClientTest extends TestBase {

    UserApi api;

    @Before
    public void setup() throws Exception {
        super.setup();
        generateData();
        api = ApiFactory.createApi(UserApi.class, authenticate(Role.ADMIN), baseURL);
    }

    @Test
    public void testCurrent() throws Exception {
        List<User> users = api.current(SchemaData.challengeUsers);
        assertNotNull(users);
        assertFalse(users.isEmpty());

        for (User user : users) {
            assertNotNull(user.getDivisions());
            assertFalse(user.getDivisions().isEmpty());
            assertTrue(user.getDivisions().size() == 2);

            assertNotNull(user.getSeasons());
            assertNotNull(user.getTeams());
        }

    }
}
