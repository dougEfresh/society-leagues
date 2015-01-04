package com.society.test;


import com.society.leagues.Main;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.admin.api.PlayerAdminApi;
import com.society.leagues.client.api.Role;
import com.society.leagues.client.api.domain.Player;
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
public class PlayerTest extends TestBase {
    PlayerAdminApi api;

    @Before
    public void setup() throws Exception {
        super.setup();
        api = ApiFactory.createApi(PlayerAdminApi.class,authenticate(Role.ADMIN),baseURL);
    }

    @Test
    public void testCreate() {
        Player newPlayer = generatePlayer(Role.Player);
        Mockito.when(mockPlayerDao.create(newPlayer)).thenReturn(newPlayer);
        Player returned = api.create(newPlayer);
        assertNotNull(returned);
        assertEquals(newPlayer.getEmail(),returned.getEmail());
        assertNull(returned.getPassword());
        assertNotNull(returned.getId());
    }

    @Test
    public void testDelete() {
        Player newPlayer = generatePlayer(Role.Player);
        Mockito.when(mockPlayerDao.delete(newPlayer.getId())).thenReturn(Boolean.TRUE);
        assertTrue(api.delete(newPlayer.getId()));
    }

    @Test
    public void testModify() {
        Player player = generatePlayer(Role.Player);
        Player modifiedPlayer = generatePlayer(Role.Player);
        modifiedPlayer.setFirstName("me");
        Mockito.when(mockPlayerDao.modify(player)).thenReturn(modifiedPlayer);
        Player returned = api.modify(player);
        assertNotNull(returned);
        assertEquals(modifiedPlayer.getFirstName(),returned.getFirstName());
    }

    @Test
    public void testNoAccess() {
        api = ApiFactory.createApi(PlayerAdminApi.class,authenticate(Role.Player),baseURL);
        try {
            api.create(generatePlayer(Role.Player));
        } catch (Throwable t){
            assertTrue(t.getCause() instanceof Unauthorized);
        }
    }
}
