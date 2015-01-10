package com.society.test;

import com.society.leagues.ServerControl;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.AuthApi;
import com.society.leagues.client.api.Role;
import com.society.leagues.client.api.domain.TokenResponse;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.dao.PlayerAdminDao;
import com.society.leagues.infrastructure.security.ServiceAuthenticator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;

import static org.junit.Assert.assertNotNull;

@Component
public class TestIntegrationBase {
    public static final String NORMAL_USER = "email_608@domain.com";
    public static final String NORMAL_PASS = "password_608";
    public static final String ADMIN_USER =  "email_528@domain.com";
    public static final String ADMIN_PASS =  "password_528";

    @Autowired ServiceAuthenticator serviceAuthenticator;
    @Autowired ServerControl app;
    @Autowired PlayerAdminDao playerDao;
    @Autowired JdbcTemplate jdbcTemplate;

    AuthApi authApi;
    String baseURL;
    Client client;

    public void createAccounts() {
        Integer exists = jdbcTemplate.queryForObject("select count(*) from users where login = ?",Integer.class,ADMIN_USER);
        if (exists.equals(0)) {
            jdbcTemplate.update("INSERT INTO users (login,role,`password`)" +
                            " VALUES (?,?,?)",
                    ADMIN_USER, Role.ADMIN.name(),ADMIN_PASS);

        }

        exists = jdbcTemplate.queryForObject("select count(*) from users where login = ?",Integer.class,NORMAL_USER);
        if (exists > 0 )
                return;

        jdbcTemplate.update("INSERT INTO users (login,role,`password`) " +
                            "VALUES (?,?,?)",
                    NORMAL_USER, Role.PLAYER.name(), NORMAL_PASS);
    }
    @Before
    public void setup() throws Exception {
        baseURL = "http://localhost:" + app.getPort();
        authApi = ApiFactory.createApi(AuthApi.class, null, baseURL, true);
        createAccounts();
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
