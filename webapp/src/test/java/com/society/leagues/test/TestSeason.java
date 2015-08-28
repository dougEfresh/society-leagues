package com.society.leagues.test;

import com.society.leagues.Main;
import com.society.leagues.client.api.domain.Role;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.mongo.SeasonRepository;
import com.society.leagues.mongo.UserRepository;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest
public class TestSeason {

    private static Logger logger = Logger.getLogger(TestUser.class);
    @Value("${local.server.port}")
    private int port;

    @Autowired UserRepository userRepository;
    @Autowired SeasonRepository seasonRepository;

    @Test
    public void testUser() {
        seasonRepository.deleteAll();
    }
}
