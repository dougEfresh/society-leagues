package com.society.test;

import com.society.leagues.ServerControl;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.AuthApi;
import com.society.leagues.client.api.Role;
import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.TokenResponse;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.dao.LeagueAdminDao;
import com.society.leagues.dao.PlayerAdminDao;
import com.society.leagues.dao.TeamAdminDao;
import com.society.leagues.infrastructure.security.ServiceAuthenticator;
import org.junit.Before;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.client.Client;

import static org.junit.Assert.assertNotNull;

public abstract class TestBase {
    public static final String NORMAL_USER = "email_608@domain.com";
    public static final String NORMAL_PASS = "password_608";
    public static final String ADMIN_USER =  "email_46@domain.com";
    public static final String ADMIN_PASS =  "password_46";

    @Autowired ServiceAuthenticator mockedServiceAuthenticator;
    @Autowired ServerControl app;
    @Autowired PlayerAdminDao mockPlayerDao;
    @Autowired LeagueAdminDao mockLeagueDao;
    @Autowired TeamAdminDao mockTeamDao;

    AuthApi authApi;
    String baseURL;
    Client client;

    @Before
    public void setup() throws Exception {
        Mockito.reset(mockedServiceAuthenticator,mockPlayerDao,mockLeagueDao,mockTeamDao);
        baseURL = "http://localhost:" + app.getPort();
        authApi = ApiFactory.createApi(AuthApi.class,null,baseURL,true);
    }

    public String authenticate(Role role) {
        User user = new User(ADMIN_USER,ADMIN_PASS);
        user.addRole(role);
        Mockito.reset(mockedServiceAuthenticator);
        Mockito.when(mockedServiceAuthenticator.authenticate(
                user.getUsername(),user.getPassword()))
                .thenReturn(user);
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
