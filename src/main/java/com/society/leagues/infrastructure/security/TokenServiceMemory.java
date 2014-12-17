package com.society.leagues.infrastructure.security;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.UUID;

public class TokenServiceMemory implements TokenService {
    private static final HashMap<String,Authentication> restApiAuthTokenCache = new HashMap<>();
    public static final int HALF_AN_HOUR_IN_MILLISECONDS = 30 * 60 * 1000;

    @Override
    @Scheduled(fixedRate = HALF_AN_HOUR_IN_MILLISECONDS)
    public void evictExpiredTokens() {

    }

    @Override
    public String generateNewToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void store(String token, Authentication authentication) {
        restApiAuthTokenCache.put(token,authentication);
    }

    @Override
    public boolean contains(String token) {
        return restApiAuthTokenCache.get(token) != null;
    }

    @Override
    public Authentication retrieve(String token) {
        return restApiAuthTokenCache.get(token);
    }
}
