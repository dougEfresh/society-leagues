package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.Role;
import com.society.leagues.client.api.admin.UserAdminApi;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.client.exception.Unauthorized;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class, TestConfig.class, AdminTestConfig.class})
@IntegrationTest(value = {"server.port:0","daemon:true","debug:true"})
public class UserAdminTest extends TestBase {
    UserAdminApi api;

    @Before
    public void setup() throws Exception {
        super.setup();
        api = ApiFactory.createApi(UserAdminApi.class, authenticate(Role.ADMIN), baseURL);
    }

    @Test
    public void testCreate() {
        User newUser = generateUser(Role.PLAYER);
        newUser.setId(100);
        Mockito.when(mockUserDao.create(Mockito.any(User.class))).thenReturn(newUser);
        User returned = api.create(newUser);
        assertNotNull(returned);
        assertEquals(newUser.getEmail(),returned.getEmail());
        assertNull(returned.getPassword());
        assertNotNull(returned.getId());
    }

    @Test
    public void testDelete() {
        User newUser = generateUser(Role.PLAYER);
        Mockito.when(mockUserDao.delete(newUser)).thenReturn(Boolean.TRUE);
        assertTrue(api.delete(newUser));
    }

    @Test
    public void testModify() {
        User user = generateUser(Role.PLAYER);
        User modifiedUser = generateUser(Role.PLAYER);
        modifiedUser.setFirstName("me");
        Mockito.when(mockUserDao.modify(user)).thenReturn(modifiedUser);
        User returned = api.modify(user);
        assertNotNull(returned);
        assertEquals(modifiedUser.getFirstName(),returned.getFirstName());
    }

    @Test
    public void testNoAccess() {
        api = ApiFactory.createApi(UserAdminApi.class,authenticate(Role.PLAYER),baseURL);
        try {
            api.create(generateUser(Role.PLAYER));
        } catch (Throwable t){
            assertTrue(t.getCause() instanceof Unauthorized);
        }
    }
}
