package com.society.test;

import com.society.leagues.ServerControl;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.AuthApi;
import com.society.leagues.client.api.Role;
import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.TokenResponse;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.dao.PlayerAdminDao;
import com.society.leagues.infrastructure.security.ServiceAuthenticator;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.client.Client;

import static org.junit.Assert.assertNotNull;

public class TestIntegrationBase {
    public static final String NORMAL_USER = "email_608@domain.com";
    public static final String NORMAL_PASS = "password_608";
    public static final String ADMIN_USER =  "email_528@domain.com";
    public static final String ADMIN_PASS =  "password_528";

    @Autowired ServiceAuthenticator serviceAuthenticator;
    @Autowired ServerControl app;
    @Autowired
    PlayerAdminDao playerDao;

    AuthApi authApi;
    String baseURL;
    Client client;

    @Before
    public void setup() throws Exception {
        baseURL = "http://localhost:" + app.getPort();
        authApi = ApiFactory.createApi(AuthApi.class, null, baseURL, true);
    }

    public String authenticate(Role role) {
        User user;
        if (Role.isAdmin(role))
            user = new User(ADMIN_USER,ADMIN_PASS);
        else
            user = new User(NORMAL_USER,NORMAL_PASS);

        TokenResponse response = authApi.authenticate(user);
        assertNotNull(response);
        assertNotNull(response.getToken());
        return response.getToken();
    }

    public Player generatePlayer(Role role) {
        Player newPlayer = new Player();
        newPlayer.setPassword("blah");
        newPlayer.setFirstName("Richard");
        newPlayer.setLastName("Feynman");
        newPlayer.addRole(role);
        newPlayer.setLogin("rfeynamen@physics.com");
        newPlayer.setEmail(newPlayer.getLogin());
        newPlayer.setId(1);
        return newPlayer;
    }

}
