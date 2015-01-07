package com.society.test;

import com.society.leagues.ServerControl;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.AuthApi;
import com.society.leagues.client.api.Role;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.client.api.domain.TokenResponse;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.dao.*;
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
    @Autowired UserAdminDao mockUserDao;
    @Autowired LeagueAdminDao mockLeagueDao;
    @Autowired DivisionAdminDao mockDivisionDao;
    @Autowired TeamAdminDao mockTeamDao;
    @Autowired SeasonAdminDao mockSeasonDao;

    AuthApi authApi;
    String baseURL;
    Client client;

    @Before
    public void setup() throws Exception {
        Mockito.reset(mockedServiceAuthenticator,mockUserDao,mockLeagueDao,mockTeamDao,mockDivisionDao,mockSeasonDao,mockUserDao);
        baseURL = "http://localhost:" + app.getPort();
        authApi = ApiFactory.createApi(AuthApi.class,null,baseURL,true);
    }

    public String authenticate(Role role) {
        User user = new User(ADMIN_USER,ADMIN_PASS);
        user.addRole(role);
        Mockito.reset(mockedServiceAuthenticator);
        Mockito.when(mockedServiceAuthenticator.authenticate(
                user.getLogin(),user.getPassword()))
                .thenReturn(user);
        TokenResponse response = authApi.authenticate(user);
        assertNotNull(response);
        assertNotNull(response.getToken());
        return response.getToken();
    }

    public User generateUser(Role role) {
        User newUser = new User();
        newUser.setPassword("blah");
        newUser.setFirstName("Richard");
        newUser.setLastName("Feynman");
        newUser.addRole(role);
        newUser.setLogin("rfeynamen@physics.com");
        newUser.setEmail(newUser.getLogin());
        newUser.setId(1);
        return newUser;
    }

}
