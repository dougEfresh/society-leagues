package com.society.test;

import com.society.leagues.ServerControl;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.AuthApi;
import com.society.leagues.client.api.Role;
import com.society.leagues.client.api.domain.TokenResponse;
import com.society.leagues.client.api.domain.User;
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
        User user = new User(ADMIN_USER,ADMIN_USER);
        user.addRole(Role.ADMIN);
        TokenResponse response = authApi.authenticate(user);
        assertNotNull(response);
        assertNotNull(response.getToken());
        return response.getToken();
    }

}
