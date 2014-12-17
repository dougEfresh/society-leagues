package com.society.test;

import com.jayway.restassured.RestAssured;
import com.society.leagues.Application;
import com.society.leagues.api.ApiController;
import com.society.leagues.infrastructure.security.ExternalServiceAuthenticator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebAppConfiguration
@IntegrationTest(value = {"server.port:0"})
public class PlayerIntegrationTest extends IntegrationBase {

    @Test
    public void testPlayerTeamHistory() throws URISyntaxException {

        List<Map<String,Object>> results = getRequestList(ApiController.PLAYER_URL + "/teamHistory");
        for (String key : new String[]{"match_count","name","division_id","percentage","team_id"}) {
            for (Map<String, Object> result : results) {
                assertTrue(result.containsKey(key));
            }
        }
    }
}
