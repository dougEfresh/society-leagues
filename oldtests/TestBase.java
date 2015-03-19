package com.society.test;


import com.society.leagues.Main;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;


@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true, value = {"embedded=true", "generate=false"})
public class TestBase {

    @Value("${local.server.port:8080}")
    public int port;
    public String serviceUrl;

    @Before
    public void setup() throws Exception {
        serviceUrl = "http://localhost:" + port;
    }


}
