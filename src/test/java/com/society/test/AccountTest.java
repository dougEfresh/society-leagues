package com.society.test;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.ResponseBodyExtractionOptions;
import com.society.leagues.Main;
import com.society.leagues.resource.ApiResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class, TestConfig.class})
@WebAppConfiguration
@IntegrationTest(value = {"server.port:0"})
public class AccountTest extends TestBase {

    @Test
    public void testInfo() {
        String generatedToken = authenticate();
        Map<String,Object> playerInfo = new HashMap<>();
        playerInfo.put("player_id",1);
        playerInfo.put("first_name","jesus");
        playerInfo.put("last_name","god");

        when(mockAccountDao.getAcctInfo(anyInt())).thenReturn(playerInfo);

        given().header(X_AUTH_TOKEN, generatedToken).
                when().post(ApiResource.ACCOUNT_URL + "/info").
                then().statusCode(HttpStatus.OK.value());

        ResponseBodyExtractionOptions body =  given().header(X_AUTH_TOKEN, generatedToken).
                when().post(ApiResource.ACCOUNT_URL + "/info").then().extract().body();

        assertNotNull(body);
        assertNotNull(body.jsonPath());
        JsonPath json = body.jsonPath();
        assertEquals(json.getInt("player_id"),playerInfo.get("player_id"));
        assertEquals(json.getString("first_name"),playerInfo.get("first_name"));
        assertEquals(json.getString("last_name"),playerInfo.get("last_name"));
    }
}
