package com.society.leagues.conf.spring.social;

import com.society.leagues.conf.spring.PrincipleDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class ImplicitSignInAdapter  implements SignInAdapter {

    @Autowired PrincipleDetailsService principleDetailsService;
    @Autowired PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices;

    public ImplicitSignInAdapter() {

    }

    @Override
    public String signIn(String localUserId, Connection<?> connection, NativeWebRequest request) {
        String providerUserId = connection.getKey().getProviderUserId();
        UserDetails details = principleDetailsService.loadUserByUsername(localUserId);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(details.getUsername(), details.getPassword(), details.getAuthorities()));
        persistentTokenBasedRememberMeServices.loginSuccess(
                (HttpServletRequest) request.getNativeRequest(),
                (HttpServletResponse) request.getNativeResponse(),
                SecurityContextHolder.getContext().getAuthentication());
        return null;
    }

    private void removeAutheticationAttributes(HttpSession session) {
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

}
