package com.society.leagues.infrastructure.security;

import com.society.leagues.client.api.Role;
import com.society.leagues.client.api.domain.User;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;


public class UserSecurityContext implements SecurityContext {

    User user;

    public UserSecurityContext(User user) {
        this.user = user;
    }

    public UserSecurityContext() {
        this.user = user;
    }

    @Override
    public Principal getUserPrincipal() {
        return new Principal() {
            @Override
            public String getName() {
                return user.getLogin();
            }
        };
    }

    @Override
    public boolean isUserInRole(String role) {
        Role r = Role.fromString(role);
        return  r == user.getRole();
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public String getAuthenticationScheme() {
        return null;
    }
}
