package com.society.leagues.infrastructure.security;

public interface ExternalServiceAuthenticator {

    AuthenticationWithToken authenticate(String username, String password);
}
