package com.society.test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.ValidatableResponse;
import com.society.leagues.Application;
import com.society.leagues.api.ApiController;
import com.society.leagues.api.player.PlayerDao;
import com.society.leagues.infrastructure.AuthenticatedExternalWebService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class,
        SecurityTest.SecurityTestConfig.class})
@WebAppConfiguration
@IntegrationTest(value = {"server.port:0","auth.provider=fake"})
public class PlayerTest {
    private static final String X_AUTH_USERNAME = "X-Auth-Username";
    private static final String X_AUTH_PASSWORD = "X-Auth-Password";
    private static final String X_AUTH_TOKEN = "X-Auth-Token";

    @Value("${local.server.port}")
    int port;

    @Autowired PlayerDao playerDao;

    @Configuration
    public static class PlayerTestConfig {
        @Bean
        @Primary
        public PlayerDao getPlayerDao() {
            return mock(PlayerDao.class);
        }
    }

    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        Mockito.reset(playerDao);
    }

    @Test
    public void testPlayerInfo() {
        String generatedToken = authenticateByUsernameAndPasswordAndGetToken();
        Map<String,Object> playerInfo = new HashMap<>();
        playerInfo.put("player_id",1);
        playerInfo.put("league_name","some league name");
        when(playerDao.getTeamHistory(anyInt())).thenReturn(playerInfo);

        given().header(X_AUTH_TOKEN, generatedToken).
                when().get(ApiController.PLAYER_URL + "/teamHistory").
                then().statusCode(HttpStatus.OK.value());

    }

    private String authenticateByUsernameAndPasswordAndGetToken() {
        String username = "email_608@domain.com";
        String password = "password_608";

        AuthenticatedExternalWebService authenticationWithToken = new AuthenticatedExternalWebService(username, null,
                AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_DOMAIN_USER"));
        authenticationWithToken.setToken("TEST-TOKEN");

         ValidatableResponse validatableResponse = given().header(X_AUTH_USERNAME, username).
                header(X_AUTH_PASSWORD, password).
                when().post(ApiController.AUTHENTICATE_URL).
                then().statusCode(HttpStatus.OK.value());

        HashMap<String,String> body = validatableResponse.extract().body().jsonPath().get();
        assertTrue("No X-Auth-Token",body.containsKey("X-Auth-Token"));
        return body.get("X-Auth-Token");
    }

}
