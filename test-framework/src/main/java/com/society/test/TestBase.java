package com.society.test;

import static com.society.leagues.Schema.*;

import com.society.leagues.Schema;
import com.society.leagues.SchemaData;
import com.society.leagues.ServerControl;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.AuthApi;
import com.society.leagues.client.api.domain.Role;
import com.society.leagues.client.api.domain.TokenResponse;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.conf.DaoConfig;
import com.society.leagues.infrastructure.security.ServiceAuthenticator;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;

import static org.junit.Assert.assertNotNull;

@Component
public class TestBase {
    public static final String NORMAL_USER = Schema.NORMAL_USER;
    public static final String NORMAL_PASS = Schema.NORMAL_PASS;
    public static final String ADMIN_USER =  Schema.ADMIN_USER;
    public static final String ADMIN_PASS =  Schema.ADMIN_PASS;
    
    @Autowired ServiceAuthenticator serviceAuthenticator;
    @Autowired ServerControl app;
    @Autowired JdbcTemplate jdbcTemplate;
    public static String token;
    AuthApi authApi;
    String baseURL;
    Client client;

    @Before
    public void setup() throws Exception {
        baseURL = "http://localhost:" + app.getPort();
        authApi = ApiFactory.createApi(AuthApi.class, null, baseURL, true);
        generateData();
    }
    
    public void generateData() {
        SchemaData schemaData = new SchemaData();
        schemaData.generateData(baseURL,authenticate(Role.ADMIN));
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

}
