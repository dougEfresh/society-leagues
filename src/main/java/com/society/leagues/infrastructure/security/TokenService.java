package com.society.leagues.infrastructure.security;


import org.springframework.security.core.Authentication;

public interface TokenService {
    public void evictExpiredTokens();
    public String generateNewToken();
    public void store(String token, Authentication authentication);
    public boolean contains(String token);
    public Authentication retrieve(String token);
}
