package com.society.test;

import com.society.leagues.dao.PlayerDao;
import org.mockito.Mock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.mock;

@Configuration
@ActiveProfiles(profiles = "test")
public class AdminTestConfig {

    @Bean
    @Primary
    PlayerDao getPlayerDao() {
        return mock(PlayerDao.class);
    }
}
