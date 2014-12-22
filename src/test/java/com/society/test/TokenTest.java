package com.society.test;

import com.society.leagues.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, TestConfig.class})
@WebAppConfiguration
@IntegrationTest(value = {"server.port:0"})
public class TokenTest {

    @Test
    public void testJson() {
        //TODO Add tests
        //{"authenticated":true,"authorities":[{"authority":"ROLE_DOMAIN_USER"}],"credentials":null,"details":"fb8f6d3a-4a17-4698-b738-7121a11b983d","externalServiceAuthenticator":{},"name":"playerId: 46 username: null","principal":{"id":46,"player":{"admin":false,"firstName":null,"id":46,"lastName":null,"login":null},"username":null},"token":"fb8f6d3a-4a17-4698-b738-7121a11b983d"}
    }
}
