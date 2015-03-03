package com.society.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class SecurityTest extends TestBase {

    @Test
    public void testAuth() {
        /*
        TokenResponse response = authApi.authenticate(new Login(ADMIN_USER, ADMIN_PASS));
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getToken());
        */
    }

    @Test
    public void testBadAuth() {
        /*
        TokenResponse response = authApi.authenticate(new Login(ADMIN_USER, "blah"));
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getToken());
        */
    }

    @Test
    public void testExpiredToken() {
        /*
        TokenResponse response = authApi.authenticate(new Login(ADMIN_USER, ADMIN_PASS));
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getToken());

        DivisionAdminApi adminApi = ApiFactory.createApi(DivisionAdminApi.class,"badtoken",baseURL);
        try {
            adminApi.create(new Division());
        } catch (ProcessingException e) {
              assertTrue(e.getCause() instanceof Unauthorized);
        }
        */
    }
}
