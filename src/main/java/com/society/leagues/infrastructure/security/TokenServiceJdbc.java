package com.society.leagues.infrastructure.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.UUID;

@Component
public class TokenServiceJdbc implements TokenService {
    private static final Logger logger = LoggerFactory.getLogger(TokenServiceJdbc.class);
    @Autowired JdbcTemplate jdbcTemplate;
    //private static final Cache restApiAuthTokenCache = CacheManager.getInstance().getCache("restApiAuthTokenCache");
    //TODO use ecache?
    private static final HashMap<String,Authentication> restApiAuthTokenCache = new HashMap<>();
    public static final int HALF_AN_HOUR_IN_MILLISECONDS = 30 * 60 * 1000;

    @Override
    @Scheduled(fixedRate = HALF_AN_HOUR_IN_MILLISECONDS)
    public void evictExpiredTokens() {
        logger.info("Evicting expired tokens");
        //restApiAuthTokenCache.evictExpiredElements();
    }

    @Override
    public String generateNewToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void store(String token, Authentication authentication) {
        //restApiAuthTokenCache.put(new Element(token, authentication));
        restApiAuthTokenCache.put(token,authentication);
    }

    @Override
    public boolean contains(String token) {
        return restApiAuthTokenCache.get(token) != null;
    }

    @Override
    public Authentication retrieve(String token) {
        //return (Authentication) restApiAuthTokenCache.get(token).getObjectValue();
        return restApiAuthTokenCache.get(token);
    }

}
