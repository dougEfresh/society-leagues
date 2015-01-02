package com.society.leagues.infrastructure.security;

public interface ServiceAuthenticator {

    PrincipalToken authenticate(String username, String password);
}
