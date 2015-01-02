package com.society.test;

import com.society.leagues.Main;

import com.society.leagues.client.api.domain.User;
import com.society.leagues.infrastructure.security.PrincipalToken;
import com.society.leagues.infrastructure.token.TokenResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class, TestConfig.class})
@IntegrationTest(value = {"server.port:0","daemon:true","debug:true"})
public class SecurityTest extends TestBase {

    @Test
    public void testAuth() {
        Mockito.when(mockedServiceAuthenticator.authenticate(NORMAL_USER,NORMAL_PASS)).
                thenReturn(new PrincipalToken("token", NORMAL_USER));

        TokenResponse response = authApi.authenticate(new User(NORMAL_USER, NORMAL_PASS));
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getToken());
    }

    @Test
    public void testFailure() {
        Mockito.when(mockedServiceAuthenticator.authenticate(NORMAL_USER, NORMAL_PASS)).
                thenThrow(new RuntimeException());

        TokenResponse response = authApi.authenticate(new User(NORMAL_USER, NORMAL_PASS));
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getToken());
        PrincipalToken principalToken = new PrincipalToken(null,NORMAL_USER);
        Mockito.reset(mockedServiceAuthenticator);
        Mockito.when(mockedServiceAuthenticator.authenticate(NORMAL_USER, NORMAL_PASS)).thenReturn(principalToken);

        response = authApi.authenticate(new User(NORMAL_USER, NORMAL_PASS));
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getToken());

        Mockito.reset(mockedServiceAuthenticator);
        Mockito.when(mockedServiceAuthenticator.authenticate(NORMAL_USER, NORMAL_PASS)).thenReturn(null);

        response = authApi.authenticate(new User(NORMAL_USER, NORMAL_PASS));
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getToken());

    }


}
