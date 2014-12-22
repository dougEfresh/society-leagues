package com.society.test;

import com.society.leagues.Application;
import com.society.leagues.api.ApiController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebAppConfiguration
@IntegrationTest(value = {"server.port:0"})
public class DivisionIntegrationTest extends IntegrationBase {
    Logger logger = LoggerFactory.getLogger(DivisionIntegrationTest.class);
    @Test
    public void divisionInfo() throws URISyntaxException {
        List<Map<String,Object>> results = getRequestList(ApiController.DIVISION_URL + "/info");
            for (Map<String, Object> result : results) {
                result.keySet().forEach(logger::info);
            }
    }
}
