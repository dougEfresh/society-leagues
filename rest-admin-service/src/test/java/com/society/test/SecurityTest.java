package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.admin.LeagueAdminApi;
import com.society.leagues.client.api.admin.MatchResultApi;
import com.society.leagues.client.api.Role;
import com.society.leagues.client.api.admin.TeamAdminApi;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.TokenResponse;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.client.api.domain.league.League;
import com.society.leagues.client.api.domain.league.LeagueType;
import com.society.leagues.client.exception.Unauthorized;
import com.society.leagues.resource.LeagueAdminResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.ProcessingException;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class, TestConfig.class, AdminTestConfig.class})
@IntegrationTest(value = {"server.port:8080","daemon:true","debug:true"})
public class SecurityTest extends TestBase {

    @Test
    public void testAuth() {
        User user = new User();
        Mockito.when(mockedServiceAuthenticator.authenticate(NORMAL_USER,NORMAL_PASS)).
                thenReturn(user);

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

        Mockito.reset(mockedServiceAuthenticator);
        Mockito.when(mockedServiceAuthenticator.authenticate(NORMAL_USER, NORMAL_PASS)).thenReturn(null);

        response = authApi.authenticate(new User(NORMAL_USER, NORMAL_PASS));
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getToken());
    }

    @Test
    public void testDenied() {
        try {
            MatchResultApi matchResultApi = ApiFactory.createApi(MatchResultApi.class, null, baseURL, true);
            matchResultApi.delete(0);
        } catch (ProcessingException e) {
            assertTrue(e.getCause() instanceof Unauthorized);
        }
    }

    @Test
    public void testAccess() {
        User user = new User(ADMIN_USER,ADMIN_PASS);
        user.addRole(Role.ADMIN);
        Mockito.when(mockedServiceAuthenticator.authenticate(
                user.getLogin(),user.getPassword()))
                .thenReturn(user);
        TokenResponse response = authApi.authenticate(user);
        assertNotNull(response);
        assertNotNull(response.getToken());
        LeagueAdminApi leagueAdminApi = ApiFactory.createApi(
                LeagueAdminApi.class,
                response.getToken(),
                baseURL,
                true);

        League league = new League(LeagueType.INDIVIDUAL);
        league.setId(100);
        assertNotNull(leagueAdminApi.delete(league));

        user = new User(ADMIN_USER,ADMIN_PASS);
        user.addRole(Role.ADMIN);
        Mockito.reset(mockedServiceAuthenticator);
        Mockito.when(mockedServiceAuthenticator.authenticate(
                ADMIN_USER,ADMIN_PASS))
                .thenReturn(user);

        response = authApi.authenticate(user);
        assertNotNull(response);
        assertNotNull(response.getToken());

        leagueAdminApi = ApiFactory.createApi(
                LeagueAdminApi.class,
                response.getToken(),
                baseURL,
                true);

        assertNotNull(leagueAdminApi.delete(league));
    }

    @Test
    public void testRole() {
        User user = new User(ADMIN_USER,ADMIN_PASS);
        user.addRole(Role.PLAYER);
        Mockito.when(mockedServiceAuthenticator.authenticate(
                ADMIN_USER,ADMIN_PASS))
                .thenReturn(user);
        TokenResponse response = authApi.authenticate(user);
        assertNotNull(response);
        assertNotNull(response.getToken());
        try {
            MatchResultApi matchResultApi = ApiFactory.createApi(
                    MatchResultApi.class,
                    response.getToken(),
                    baseURL,
                    true);
            matchResultApi.delete(0);
        } catch (Throwable t){
            assertTrue(t.getCause() instanceof Unauthorized);
        }
    }
}
