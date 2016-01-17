package com.society.leagues.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import java.util.Collection;

public class CookieContext implements SecurityContext {
    final String cookies;
    boolean cookieChange;
    Collection<String> newCookies;
    CookieAuth cookieAuth;

    public CookieContext(String cookies) {
        this.cookies = cookies;
        cookieAuth = new CookieAuth(cookies);

    }

    public String getCookies() {
        return cookies;
    }

    public void setCookieChange(boolean cookieChange) {
        this.cookieChange = cookieChange;
    }

    public boolean isCookieChange() {
        return cookieChange;
    }

    public Collection<String> getNewCookies() {
        return newCookies;
    }

    public void setNewCookies(Collection<String> newCookies) {
        this.newCookies = newCookies;
    }

    public CookieContext() {
        this.cookies = null;
    }

    @Override
    public Authentication getAuthentication() {
        return cookieAuth;
    }

    @Override
    public void setAuthentication(Authentication authentication) {
        if (authentication instanceof CookieAuth) {
            cookieAuth = (CookieAuth) authentication;
        }
    }
}
