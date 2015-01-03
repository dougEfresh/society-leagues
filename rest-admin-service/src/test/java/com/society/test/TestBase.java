package com.society.test;

import com.society.leagues.ServerControl;
import com.society.leagues.client.api.AccountApi;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.AuthApi;
import com.society.leagues.client.api.domain.User;

import com.society.leagues.client.api.domain.Player;
import com.society.leagues.infrastructure.security.ServiceAuthenticator;
import com.society.leagues.client.api.domain.TokenResponse;
import org.junit.Before;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.client.Client;

import static org.junit.Assert.assertNotNull;

public abstract class TestBase {
    public static final String NORMAL_USER="email_608@domain.com";
    public static final String NORMAL_PASS="password_608";
    public static final String ADMIN_USER="email_46@domain.com";
    public static final String ADMIN_PASS ="password_46";

    @Autowired ServiceAuthenticator mockedServiceAuthenticator;
    @Autowired ServerControl app;

    AuthApi authApi;
    String baseURL;
    Client client;

    @Before
    public void setup() throws Exception {
        Mockito.reset(mockedServiceAuthenticator);
        baseURL = "http://localhost:" + app.getPort();
        authApi = ApiFactory.createApi(AuthApi.class,null,baseURL,true);
    }

    public String authenticate() {
        String username = "email_608@domain.com";
        String password = "password_608";
        TokenResponse response = authApi.authenticate(new User(username, password));
        assertNotNull(response);
        assertNotNull(response.getToken());

        return response.getToken();
    }

}
