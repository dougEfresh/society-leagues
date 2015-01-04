package com.society.test;

import com.society.leagues.dao.LeagueAdminDao;
import com.society.leagues.dao.PlayerAdminDao;
import com.society.leagues.dao.TeamAdminDao;
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
    PlayerAdminDao getPlayerDao() {
        return mock(PlayerAdminDao.class);
    }

    @Bean
    @Primary
    LeagueAdminDao getLeagueDao() {
        return mock(LeagueAdminDao.class);
    }

    @Bean
    @Primary
    TeamAdminDao getTeamDao() {
        return mock(TeamAdminDao.class);
    }
}
