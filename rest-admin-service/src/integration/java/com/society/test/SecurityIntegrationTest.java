package com.society.test;


import com.society.leagues.Main;
import com.society.leagues.client.api.domain.TokenResponse;
import com.society.leagues.client.api.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@IntegrationTest(value = {"server.port:0","daemon:true","debug:true"})
public class SecurityIntegrationTest  extends TestIntegrationBase {

    @Test
    public void testAuth() {
        TokenResponse response = authApi.authenticate(new User(ADMIN_USER, ADMIN_PASS));
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getToken());
    }

    @Test
    public void testBadAuth() {
        TokenResponse response = authApi.authenticate(new User(ADMIN_USER, "blah"));
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getToken());
    }
}