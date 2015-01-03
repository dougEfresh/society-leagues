package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.infrastructure.token.TokenService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.Test;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@WebAppConfiguration
@IntegrationTest(value = {"server.port:0"})
public class TokenServiceIntegrationTest extends IntegrationBase {
    @Autowired
    TokenService tokenService;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    public void testTokenService(){
        assertFalse(tokenService.contains("blah"));

        assertTrue(tokenService.contains(token));
        assertNotNull(tokenService.retrieve(token));
        
        //TODO Test with logout
        jdbcTemplate.update("update token_cache set created_date = ADDDATE(now(),-40)");
        tokenService.evictExpiredTokens();
        assertFalse(tokenService.contains(token));

    }
}
