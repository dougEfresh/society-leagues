package com.society.leagues.infrastructure;

import com.society.leagues.infrastructure.security.AuthenticationWithToken;
import com.society.leagues.infrastructure.security.ExternalServiceAuthenticator;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class AuthenticatedExternalWebService extends AuthenticationWithToken {
    private ExternalServiceAuthenticator externalServiceAuthenticator;

    public AuthenticatedExternalWebService(Object aPrincipal, Object aCredentials, Collection<? extends GrantedAuthority> anAuthorities) {
        super(aPrincipal, aCredentials, anAuthorities);
    }

    public void setExternalServiceAuthenticator(ExternalServiceAuthenticator externalServiceAuthenticator) {
        this.externalServiceAuthenticator = externalServiceAuthenticator;
    }
}
