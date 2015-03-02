package com.society.test;


import com.society.leagues.Schema;
import com.society.leagues.SchemaData;
import com.society.leagues.ServerControl;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.AuthApi;
import com.society.leagues.client.api.domain.Login;
import com.society.leagues.client.api.domain.Role;
import com.society.leagues.client.api.domain.TokenResponse;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.infrastructure.security.ServiceAuthenticator;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;

import static org.junit.Assert.assertNotNull;

@Component
@IntegrationTest(value = {"server.port:0","daemon:true","debug:true","embedded:true"})
@WebIntegrationTest(randomPort = true)
public class TestBase {
    public static final String NORMAL_USER = Schema.NORMAL_USER;
    public static final String NORMAL_PASS = Schema.NORMAL_PASS;
    public static final String ADMIN_USER =  Schema.ADMIN_USER;
    public static final String ADMIN_PASS =  Schema.ADMIN_PASS;
    
    @Autowired ServerControl app;
    public static String token;
    AuthApi authApi;
    String baseURL;
    @Value("${server.local.port}")
    public String url;
    Client client;

    @Before
    public void setup() throws Exception {
        baseURL = "http://localhost:" + app.getPort();
        authApi = ApiFactory.createApi(AuthApi.class, null, baseURL, true);
    }
    
    public String authenticate(Role role) {
        Login user;
        if (Role.isAdmin(role))
           user = new Login(ADMIN_USER,ADMIN_PASS);
        else
            user = new Login(NORMAL_USER,NORMAL_PASS);

        TokenResponse response = authApi.authenticate(user);
        assertNotNull(response);
        assertNotNull(response.getToken());
        return response.getToken();
    }

}
