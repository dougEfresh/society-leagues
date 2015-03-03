package com.society.test;

import com.society.leagues.client.api.domain.User;
import com.society.leagues.dao.PlayerDao;
import com.society.leagues.dao.UserDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class UserTest extends TestBase {
    @Autowired UserDao api;
    @Autowired PlayerDao playerDao;

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void testCreate() {
        User user = new User(UUID.randomUUID().toString(),"password");
        User returned  = api.create(user);
        assertNotNull(returned);
        assertNotNull(returned.getId());
        assertNull(returned.getPassword());

        returned.setLogin(null);
        assertNull(api.create(returned));
    }

    @Test
    public void testDelete() {
        User user = new User(UUID.randomUUID().toString(),"password");
        User returned  = api.create(user);
        assertNotNull(returned);
        assertTrue(api.delete(returned));

        returned.setId(null);
        assertFalse(api.delete(returned));

    }

    @Test
    public void testModify() {
        User user = new User(UUID.randomUUID().toString(),"password");
        user  = api.create(user);
        assertNotNull(user);

        String login = UUID.randomUUID().toString();
        user.setLogin(login);
        user = api.modify(user);
        assertNotNull(user);
        assertEquals(login,user.getLogin());

        user.setId(null);
        assertNull(api.modify(user));
    }
}
