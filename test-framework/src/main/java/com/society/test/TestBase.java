package com.society.test;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;

import static org.junit.Assert.assertNotNull;

@Component
public class TestBase {
    public static final String NORMAL_USER = "email_608@domain.com";
    public static final String NORMAL_PASS = "password_608";
    public static final String ADMIN_USER =  "email_528@domain.com";
    public static final String ADMIN_PASS =  "password_528";
    private static Logger logger = LoggerFactory.getLogger(TestBase.class);
    @Autowired ServiceAuthenticator serviceAuthenticator;
    @Autowired ServerControl app;
    @Autowired JdbcTemplate jdbcTemplate;

    AuthApi authApi;
    String baseURL;
    Client client;
    static JdbcTemplate derbyTemplate = DaoConfig.getDerbyTemplate();

    @BeforeClass
    public static void createDb() throws Exception {
        Schema.createDb(derbyTemplate);
        Schema.createAccounts(derbyTemplate);
    }

    @AfterClass
    public static void killDb() throws Exception {

    }

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
