package com.society.leagues.infrastructure.token;


import com.society.leagues.infrastructure.security.UserSecurityContext;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class TokenServiceMemory implements TokenService {
    private static final AtomicReference<HashMap<String,UserSecurityContext>> cache = new AtomicReference<>(new HashMap<>());
    public static final int HALF_AN_HOUR_IN_MILLISECONDS = 30 * 60 * 1000;

    @Override
    @Scheduled(fixedRate = HALF_AN_HOUR_IN_MILLISECONDS)
    public void evictExpiredTokens() {

    }

    @Override
    public void clearCache() {
        cache.get().clear();
    }

    @Override
    public String generateNewToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void store(String token, UserSecurityContext userSecurityContext) {
        cache.get().put(token, userSecurityContext);
    }

    @Override
    public boolean contains(String token) {
        return cache.get().containsKey(token);
    }

    @Override
    public UserSecurityContext retrieve(String token) {
        return cache.get().get(token);
    }
}
