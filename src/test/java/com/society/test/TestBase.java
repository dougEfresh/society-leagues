package com.society.test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.ValidatableResponse;
import com.society.leagues.api.ApiController;
import com.society.leagues.api.account.AccountDao;
import com.society.leagues.api.player.PlayerDao;
import com.society.leagues.domain.DomainUser;
import com.society.leagues.domain.interfaces.Player;
import com.society.leagues.domain.player.PlayerDb;
import com.society.leagues.infrastructure.AuthenticatedExternalWebService;
import com.society.leagues.infrastructure.security.ExternalServiceAuthenticator;
import org.junit.Before;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;

public abstract class TestBase {
    public static final String X_AUTH_USERNAME = "X-Auth-Username";
    public static final String X_AUTH_PASSWORD = "X-Auth-Password";
    public static final String X_AUTH_TOKEN = "X-Auth-Token";

    @Autowired
    public ExternalServiceAuthenticator mockedExternalServiceAuthenticator;

    @Value("${local.server.port}")
    int port;

    @Autowired public PlayerDao mockPlayerDao;
    @Autowired public AccountDao mockAccountDao;

    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        Mockito.reset(mockAccountDao,mockPlayerDao);
        Mockito.reset(mockedExternalServiceAuthenticator);
    }

    public String authenticate() {
        String username = "email_608@domain.com";
        String password = "password_608";

        AuthenticatedExternalWebService authenticatedPlayer =
                new AuthenticatedExternalWebService(new DomainUser(getTestPlayer()),null,
                                                    AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_DOMAIN_USER")
                );

        BDDMockito.when(mockedExternalServiceAuthenticator.authenticate(eq(username), eq(password))).thenReturn(authenticatedPlayer);

        ValidatableResponse validatableResponse = given().header(X_AUTH_USERNAME, username).
                                                                                                   header(X_AUTH_PASSWORD, password).
                                                                                                                                            when().post(ApiController.AUTHENTICATE_URL).
                                                                                                                                                                                               then().statusCode(HttpStatus.OK.value());

        HashMap<String,String> body = validatableResponse.extract().body().jsonPath().get();
        assertTrue("No X-Auth-Token",body.containsKey("X-Auth-Token"));
        return body.get("X-Auth-Token");
    }

    public Player getTestPlayer() {
        Map<String,Object> playerInfo = new HashMap<>();
        playerInfo.put("player_id",1);
        playerInfo.put("league_name","some league name");
        return new PlayerDb(playerInfo);
    }
}
