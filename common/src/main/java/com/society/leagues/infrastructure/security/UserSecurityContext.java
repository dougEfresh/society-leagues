package com.society.leagues.infrastructure.security;

import com.society.leagues.client.api.domain.Role;
import com.society.leagues.client.api.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;


public class UserSecurityContext implements SecurityContext {
    private static Logger logger = LoggerFactory.getLogger(UserSecurityContext.class);
    User user;

    public UserSecurityContext(User user) {
        this.user = user;
    }

    public UserSecurityContext() {
    }

    @Override
    public Principal getUserPrincipal() {
        return user::getLogin;
    }

    @Override
    public boolean isUserInRole(String role) {
        try {
            Role r = Role.valueOf(role);
            return r == user.getRole();
        } catch (Throwable t) {
            logger.error(t.getLocalizedMessage(),t);
        }
        return false;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public String getAuthenticationScheme() {
        return null;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
