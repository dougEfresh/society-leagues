package com.society.test.client;

import com.society.leagues.Main;
import com.society.leagues.SchemaData;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;

@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true, value = {"embedded=true", "generate=true"})
public class TestClientBase  {

    @Autowired SchemaData schemaData;

    @Before
    public void setup() throws Exception {
        schemaData.generateData();

    }
}
