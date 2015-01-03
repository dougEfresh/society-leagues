package com.society.leagues.infrastructure.token;

import com.society.leagues.infrastructure.security.UserSecurityContext;

public interface TokenService {
    void evictExpiredTokens();
    String generateNewToken();
    void store(String token, UserSecurityContext userSecurityContext);
    boolean contains(String token);
    UserSecurityContext retrieve(String token);
}
