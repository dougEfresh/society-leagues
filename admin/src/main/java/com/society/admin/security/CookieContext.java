package com.society.admin.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

public class CookieContext implements SecurityContext {
    final String cookies;

    public CookieContext(String cookies) {
        this.cookies = cookies;

    }

    public CookieContext() {
        this.cookies = null;
    }

    @Override
    public Authentication getAuthentication() {
        return new CookieAuth(cookies);
    }

    @Override
    public void setAuthentication(Authentication authentication) {

    }
}
