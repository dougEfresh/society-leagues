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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class, TestConfig.class})
@WebAppConfiguration
@IntegrationTest(value = {"server.port:0"})
public class SchedulerTest extends TestBase {

    @Test
    public void testMatchScheduler() {
        String generatedToken = authenticate();
        List<Map<String,Object>> results = new ArrayList<>();
        Map<String,Object> resultInfo = new HashMap<>();
        resultInfo.put("player_id", 1);
        resultInfo.put("league_name", "some league name");
        results.add(resultInfo);

        when(mockSchedulerDao.getSchedule(anyInt())).thenReturn(results);

        given().header(X_AUTH_TOKEN, generatedToken).param("divisionId",new Integer(1)).
                when().post(ApiResource.SCHEDULER_URL + "/list").
                then().statusCode(HttpStatus.OK.value());

        ResponseBodyExtractionOptions body =  given().header(X_AUTH_TOKEN, generatedToken).
                param("divisionId", new Integer(1)).
                when().post(ApiResource.SCHEDULER_URL + "/list").then().extract().body();

        assertNotNull(body);
        assertNotNull(body.jsonPath());
        JsonPath json = body.jsonPath();
        List<Map<String,Object>> controllerResults = json.getList("");
        assertNotNull(controllerResults);
        assertFalse(controllerResults.isEmpty());
        assertEquals(controllerResults.get(0).get("player_id"), resultInfo.get("player_id"));
        assertEquals(controllerResults.get(0).get("league_name"),resultInfo.get("league_name"));
    }
}
