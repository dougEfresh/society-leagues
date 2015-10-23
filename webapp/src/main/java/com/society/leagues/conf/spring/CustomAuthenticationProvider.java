package com.society.leagues.conf.spring;

import org.springframework.security.authentication.RememberMeAuthenticationProvider;

public class CustomAuthenticationProvider extends RememberMeAuthenticationProvider {

    public CustomAuthenticationProvider(String key) {
        super(key);
    }
}
