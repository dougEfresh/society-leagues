package com.society.test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.ValidatableResponse;
import com.society.leagues.Main;
import com.society.leagues.ServerControl;
import com.society.leagues.client.api.AuthApi;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.infrastructure.token.TokenResponse;
import com.society.leagues.resource.ApiResource;
import com.society.leagues.dao.AccountDao;
import com.society.leagues.dao.DivisionDao;
import com.society.leagues.dao.PlayerDao;
import com.society.leagues.dao.SchedulerDao;
import com.society.leagues.domain.DomainUser;
import com.society.leagues.domain.interfaces.Player;
import com.society.leagues.domain.player.PlayerDb;
import com.society.leagues.infrastructure.AuthenticatedExternalWebService;
import com.society.leagues.infrastructure.security.JdbcServiceAuthenticator;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ClientBinding;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.restz.client.RestProxyFactory;
import org.restz.client.filter.DeprecatedResponseHandler;
import org.restz.client.filter.MethodCallFilter;
import org.restz.client.filter.VersionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.ws.rs.client.ClientBuilder;
import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.baseURI;
import static org.junit.Assert.assertNotNull;

public abstract class TestBase {
    public static final String X_AUTH_USERNAME = "username";
    public static final String X_AUTH_PASSWORD = "password";
    public static final String X_AUTH_TOKEN = "X-Auth-Token";

    public static final String NORMAL_USER="email_608@domain.com";
    public static final String NORMAL_PASS="password_608";
    public static final String ADMIN_USER="email_46@domain.com";
    public static final String ADMIN_PASS ="password_46";

    @Autowired JdbcServiceAuthenticator mockedExternalServiceAuthenticator;
    @Autowired PlayerDao mockPlayerDao;
    @Autowired AccountDao mockAccountDao;
    @Autowired SchedulerDao mockSchedulerDao;
    @Autowired DivisionDao mockDivisionDao;
    @Autowired ServerControl app;

    AuthApi authApi;
    vgf2f3gt222222222222wwwwwwwwwwwwwwww
    @Before
    public void setup() throws Exception {
        ClientConfig config = new ClientConfig().
                register(DeprecatedResponseHandler.class).
                register(MethodCallFilter.class).
                register(new VersionFilter(AuthApi.class));

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = app.getPort();
        Mockito.reset(mockAccountDao,mockPlayerDao,mockSchedulerDao,mockDivisionDao);
        Mockito.reset(mockedExternalServiceAuthenticator);
        authApi = RestProxyFactory.getRestClientApi(AuthApi.class,baseURI + ":" + app.getPort(),
                ClientBuilder.newClient(config));
    }

    @After
    public void destroy() {

    }

    public String authenticate() {
        String username = "email_608@domain.com";
        String password = "password_608";
        TokenResponse response = authApi.authenticate(new User(username, password));
        assertNotNull(response);
        assertNotNull(response.getToken());
        return response.getToken();

        /*
        AuthenticatedExternalWebService authenticatedPlayer =
                new AuthenticatedExternalWebService(new DomainUser(getTestPlayer()),null,
                                                    AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_DOMAIN_USER")
                );

        BDDMockito.when(mockedExternalServiceAuthenticator.authenticate(eq(username), eq(password))).thenReturn(authenticatedPlayer);

        ValidatableResponse validatableResponse = given().
                header(X_AUTH_USERNAME, NORMAL_USER).
                header(X_AUTH_PASSWORD, NORMAL_PASS).
                when().post(ApiResource.AUTHENTICATE_URL).
                then().statusCode(HttpStatus.FOUND.value());

        HashMap<String,String> body = validatableResponse.extract().body().jsonPath().get();
        assertTrue("No X-Auth-Token",body.containsKey("X-Auth-Token"));
        return body.get("X-Auth-Token");
        */

    }

    public String authenticateAdmin() {
        /*
        AuthenticatedExternalWebService authenticatedPlayer =
                new AuthenticatedExternalWebService(new DomainUser(getTestPlayer()),null,
                                                    AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_DOMAIN_USER,ROLE_DOMAIN_ADMIN")
                );

        BDDMockito.when(mockedExternalServiceAuthenticator.
                authenticate(eq(ADMIN_USER), eq(ADMIN_PASS))).
                thenReturn(authenticatedPlayer);

        ValidatableResponse validatableResponse = given().
                header(X_AUTH_USERNAME, ADMIN_USER).
                header(X_AUTH_PASSWORD, ADMIN_PASS).
                when().post(ApiResource.AUTHENTICATE_URL).
                then().statusCode(HttpStatus.OK.value());

        HashMap<String,String> body = validatableResponse.extract().body().jsonPath().get();
        assertTrue("No X-Auth-Token",body.containsKey("X-Auth-Token"));
        return body.get("X-Auth-Token");
        */
        return null;
    }

    public Player getTestPlayer() {
        Map<String, Object> playerInfo = new HashMap<>();
        playerInfo.put("player_id", 1);
        playerInfo.put("league_name", "some league name");
        PlayerDb playerDb = new PlayerDb();
        playerDb.setLogin("player_login");
        playerDb.setId(1);
        return playerDb;
    }
}
