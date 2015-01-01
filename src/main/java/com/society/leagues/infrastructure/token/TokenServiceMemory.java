package com.society.leagues.infrastructure.token;


import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.UUID;

public class TokenServiceMemory implements TokenService {
    private static final HashMap<String,String> restApiAuthTokenCache = new HashMap<>();
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
    public void store(String token, String authentication) {
        restApiAuthTokenCache.put(token,authentication);
    }

    @Override
    public boolean contains(String token) {
        return restApiAuthTokenCache.get(token) != null;
    }

    @Override
    public String  retrieve(String token) {
        return restApiAuthTokenCache.get(token);
    }
}
