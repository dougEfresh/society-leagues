package com.society.leagues.infrastructure.security;


import com.society.leagues.client.api.domain.User;

public interface ServiceAuthenticator {

    User authenticate(String username, String password);
}
