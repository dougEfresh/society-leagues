package com.society.leagues.infrastructure.token;


public interface TokenService {
    public void evictExpiredTokens();
    public String generateNewToken();
    public void store(String token, String authentication);
    public boolean contains(String token);
    public String retrieve(String token);
}
