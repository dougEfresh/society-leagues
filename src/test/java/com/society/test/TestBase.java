package com.society.test;

import com.society.leagues.ServerControl;
import com.society.leagues.client.api.AccountApi;
import com.society.leagues.client.api.AuthApi;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.dao.AccountDao;
import com.society.leagues.dao.DivisionDao;
import com.society.leagues.dao.PlayerDao;
import com.society.leagues.dao.SchedulerDao;
import com.society.leagues.domain.interfaces.Player;
import com.society.leagues.domain.player.PlayerDb;
import com.society.leagues.infrastructure.security.ServiceAuthenticator;
import com.society.leagues.infrastructure.token.TokenResponse;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.Before;
import org.mockito.Mockito;
import org.restz.client.RestProxyFactory;
import org.restz.client.filter.DeprecatedResponseHandler;
import org.restz.client.filter.MethodCallFilter;
import org.restz.client.filter.VersionFilter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public abstract class TestBase {
    public static final String X_AUTH_USERNAME = "username";
    public static final String X_AUTH_PASSWORD = "password";
    public static final String X_AUTH_TOKEN = "X-Auth-Token";

    public static final String NORMAL_USER="email_608@domain.com";
    public static final String NORMAL_PASS="password_608";
    public static final String ADMIN_USER="email_46@domain.com";
    public static final String ADMIN_PASS ="password_46";

    @Autowired ServiceAuthenticator mockedServiceAuthenticator;
    @Autowired PlayerDao mockPlayerDao;
    @Autowired AccountDao mockAccountDao;
    @Autowired SchedulerDao mockSchedulerDao;
    @Autowired DivisionDao mockDivisionDao;
    @Autowired ServerControl app;

    AuthApi authApi;
    AccountApi accountApi;
    String baseURL;
    ClientConfig config;
    Client client;

    @Before
    public void setup() throws Exception {
        config = new ClientConfig().
                register(DeprecatedResponseHandler.class).
                register(MethodCallFilter.class).
                register(new VersionFilter(AuthApi.class)).
                register(TokenFilter.class);

        Mockito.reset(mockAccountDao,mockPlayerDao,mockSchedulerDao,mockDivisionDao);
        Mockito.reset(mockedServiceAuthenticator);
        baseURL = "http://localhost:" + app.getPort();
        client = ClientBuilder.newClient(config);
        authApi = RestProxyFactory.getRestClientApi(
                AuthApi.class,
                baseURL,
                client);

    }

    public String authenticate() {
        String username = "email_608@domain.com";
        String password = "password_608";
        TokenResponse response = authApi.authenticate(new User(username, password));
        assertNotNull(response);
        assertNotNull(response.getToken());

        return response.getToken();
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
