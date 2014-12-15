package com.society.test;

import com.society.leagues.api.account.AccountDao;
import com.society.leagues.api.player.PlayerDao;
import com.society.leagues.infrastructure.security.ExternalServiceAuthenticator;
import com.society.leagues.infrastructure.security.TokenService;
import com.society.leagues.infrastructure.security.TokenServiceMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@Configuration
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
    public TokenService getTokenService() {
        return new TokenServiceMemory();
    }

      @Bean
      @Primary
      public ExternalServiceAuthenticator getExternalServiceAuthenticator() {
          return mock(ExternalServiceAuthenticator.class);
      }

}
