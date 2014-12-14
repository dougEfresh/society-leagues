package com.society.test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.ValidatableResponse;
import com.society.leagues.api.ApiController;
import com.society.leagues.infrastructure.AuthenticatedExternalWebService;
import com.society.leagues.infrastructure.security.ExternalServiceAuthenticator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.booleanThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import com.society.leagues.Application;

import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, SecurityTest.SecurityTestConfig.class})
@WebAppConfiguration
@IntegrationTest(value = {"server.port:0","auth.provider=fake"})
public class SecurityTest {

    private static final String X_AUTH_USERNAME = "X-Auth-Username";
    private static final String X_AUTH_PASSWORD = "X-Auth-Password";
    private static final String X_AUTH_TOKEN = "X-Auth-Token";

    @Value("${local.server.port}")
    int port;

    @Autowired @Qualifier("testServiceAuthenticator")
    ExternalServiceAuthenticator mockedExternalServiceAuthenticator;

    //ServiceGateway mockedServiceGateway;
    @Configuration
    public static class SecurityTestConfig {

        @Bean(name = "testServiceAuthenticator")
        @Primary
        public ExternalServiceAuthenticator jdbcServiceAuthenticator() {
            return mock(ExternalServiceAuthenticator.class);
        }

        @Bean(name = "tokenAuthenticationProvider")
        @Primary
        public AuthenticationProvider testAuthenticationProvider() {
            return mock(AuthenticationProvider.class);
        }


    /*
    @Bean
    @Primary
    public ServiceGateway serviceGateway() {
        return mock(ServiceGateway.class);
    }
    */

    }

    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        Mockito.reset(mockedExternalServiceAuthenticator);
    }

    @Test
    public void healthEndpoint_isAvailableToEveryone() {
        when().get("/health").
                then().statusCode(HttpStatus.OK.value()).body("status", equalTo("UP"));

        when().get("/index.html").
                then().statusCode(HttpStatus.OK.value());
    }

    @Test
    public void authenticate_withoutPassword_returnsUnauthorized() {
        given().header(X_AUTH_USERNAME, "SomeUser").
                when().post(ApiController.AUTHENTICATE_URL).
                then().statusCode(HttpStatus.UNAUTHORIZED.value());

        BDDMockito.verifyNoMoreInteractions(mockedExternalServiceAuthenticator);
    }

    @Test
    public void authenticate_withoutUsername_returnsUnauthorized() {
        given().header(X_AUTH_PASSWORD, "SomePassword").
                when().post(ApiController.AUTHENTICATE_URL).
                then().statusCode(HttpStatus.UNAUTHORIZED.value());

        BDDMockito.verifyNoMoreInteractions(mockedExternalServiceAuthenticator);
    }

    @Test
    public void authenticate_withoutUsernameAndPassword_returnsUnauthorized() {
        when().post(ApiController.AUTHENTICATE_URL).
                then().statusCode(HttpStatus.UNAUTHORIZED.value());

        BDDMockito.verifyNoMoreInteractions(mockedExternalServiceAuthenticator);
    }

    @Test
    public void authenticate_withValidUsernameAndPassword_returnsToken() {
        authenticateByUsernameAndPasswordAndGetToken();
    }

    @Test
    public void authenticate_withInvalidUsernameOrPassword_returnsUnauthorized() {
        String username = "INVALID_USER";
        String password = "InvalidPassword";

        BDDMockito.when(mockedExternalServiceAuthenticator.authenticate(anyString(), anyString())).
                thenThrow(new BadCredentialsException("Invalid Credentials"));

        given().header(X_AUTH_USERNAME, username).header(X_AUTH_PASSWORD, password).
                when().post(ApiController.AUTHENTICATE_URL).
                then().statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    /*
    @Test
    public void gettingStuff_withoutToken_returnsUnauthorized() {
        when().get(ApiController.STUFF_URL).
                then().statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void gettingStuff_withInvalidToken_returnsUnathorized() {
        given().header(X_AUTH_TOKEN, "InvalidToken").
                when().get(ApiController.STUFF_URL).
                then().statusCode(HttpStatus.UNAUTHORIZED.value());
    }


    @Test
    public void gettingStuff_withValidToken_returnsData() {
        String generatedToken = authenticateByUsernameAndPasswordAndGetToken();

        given().header(X_AUTH_TOKEN, generatedToken).
                when().get(ApiController.STUFF_URL).
                then().statusCode(HttpStatus.OK.value());
    }
    */

    private String authenticateByUsernameAndPasswordAndGetToken() {
        String username = "email_608@domain.com";
        String password = "password_608";

        AuthenticatedExternalWebService authenticationWithToken = new AuthenticatedExternalWebService(username, null,
                AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_DOMAIN_USER"));
        authenticationWithToken.setToken("TEST-TOKEN");

        BDDMockito.when(mockedExternalServiceAuthenticator.authenticate(eq(username), eq(password))).
                thenReturn(authenticationWithToken);

        ValidatableResponse validatableResponse = given().header(X_AUTH_USERNAME, username).
                header(X_AUTH_PASSWORD, password).
                when().post(ApiController.AUTHENTICATE_URL).
                then().statusCode(HttpStatus.OK.value());

       HashMap<String,String> body = validatableResponse.extract().body().jsonPath().get();
        assertTrue("No X-Auth-Token",body.containsKey("X-Auth-Token"));
        return body.get("X-Auth-Token");
    }

}
