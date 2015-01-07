package com.society.test;

import com.society.leagues.dao.*;
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
    PlayerAdminDao getPlayerAdminDao() {
        return mock(PlayerAdminDao.class);
    }

    @Bean
    @Primary
    LeagueAdminDao getLeagueAdminDao() {
        return mock(LeagueAdminDao.class);
    }

    @Bean
    @Primary
    DivisionAdminDao getDivisionAdminDao() {
        return mock(DivisionAdminDao.class);
    }

    @Bean
    @Primary
    TeamAdminDao getTeamAdminDao() {
        return mock(TeamAdminDao.class);
    }

    @Bean
    @Primary
    SeasonAdminDao getSeasonAdminDao() {
        return mock(SeasonAdminDao.class);
    }
}
