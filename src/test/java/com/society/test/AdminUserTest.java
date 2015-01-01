package com.society.test;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.ResponseBodyExtractionOptions;
import com.society.leagues.Application;
import com.society.leagues.controller.ApiController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, TestConfig.class})
@WebAppConfiguration
@IntegrationTest(value = {"server.port:0"})
public class AdminUserTest extends TestBase {

    @Test
    public void testCreate() {
        String token = authenticateAdmin();

        ResponseBodyExtractionOptions body = given().
                header(X_AUTH_TOKEN, token).
                param("fname", "test").
                param("lname", "test").
                param("login", "testUser@domain.com").
                param("email", "testUser@domain.com").
                param("password", "test").
                when().post(ApiController.ADMIN_USER_URL + "/create").
                then().statusCode(HttpStatus.OK.value()).and().
                extract().body();

        assertNotNull(body);
        assertNotNull(body.jsonPath());
        JsonPath json = body.jsonPath();
//        assertEquals(json.getInt(""),1);
        token = authenticate();

        given().
                header(X_AUTH_TOKEN, token).
                param("fname", "test").
                param("lname", "test").
                param("login", "testUser@domain.com").
                param("email", "testUser@domain.com").
                param("password", "test").
                when().post(ApiController.ADMIN_USER_URL + "/create").
                then().statusCode(HttpStatus.FORBIDDEN.value());
    }
}
