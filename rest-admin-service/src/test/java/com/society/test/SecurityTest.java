package com.society.test;


import com.society.leagues.Main;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.admin.DivisionAdminApi;
import com.society.leagues.client.api.domain.TokenResponse;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.client.exception.Unauthorized;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.ProcessingException;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@IntegrationTest(value = {"server.port:0","daemon:true","debug:true"})
public class SecurityTest extends TestBase {

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

    @Test
    public void testExpiredToken() {
        TokenResponse response = authApi.authenticate(new User(ADMIN_USER, ADMIN_PASS));
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getToken());

        DivisionAdminApi adminApi = ApiFactory.createApi(DivisionAdminApi.class,"badtoken",baseURL);
        try {
            adminApi.create(new Division());
        } catch (ProcessingException e) {
              assertTrue(e.getCause() instanceof Unauthorized);
        }
    }
}
