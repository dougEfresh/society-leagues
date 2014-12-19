package com.society.test;

import com.society.leagues.Application;
import com.society.leagues.api.account.AccountDao;
import com.society.leagues.api.division.DivisionDao;
import com.society.leagues.api.player.PlayerDao;
import com.society.leagues.api.scheduler.SchedulerDao;
import com.society.leagues.infrastructure.security.ExternalServiceAuthenticator;
import com.society.leagues.infrastructure.security.TokenService;
import com.society.leagues.infrastructure.security.TokenServiceMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;

import static org.mockito.Mockito.mock;

@Configuration
@ContextConfiguration(classes = Application.class)
@ActiveProfiles(profiles = "test")
public class TestConfig {
    @Bean
    @Primary
    public PlayerDao getPlayerDao() {
        return mock(PlayerDao.class);
    }

    @Bean
    @Primary
    public AccountDao getAccountDao() {
        return mock(AccountDao.class);
    }

    @Bean
    @Primary
    public SchedulerDao getSchedulerDao() {
        return mock(SchedulerDao.class);
    }

    @Bean
    @Primary
    public DivisionDao getDivisionDao() {
        return mock(DivisionDao.class);
    }

    @Bean
    @Primary
    public TokenService getTokenService() {
        return new TokenServiceMemory();
    }

    @Bean
    @Primary
    public ExternalServiceAuthenticator getExternalServiceAuthenticator() {
          return mock(ExternalServiceAuthenticator.class);
      }

    @Bean
    @Primary
    public JdbcTemplate getJdbcTemplate() {
          return mock(JdbcTemplate.class);
      }

    @Bean
    @Primary
    DataSource getDataSource() {
        return mock(DataSource.class);
    }

}
