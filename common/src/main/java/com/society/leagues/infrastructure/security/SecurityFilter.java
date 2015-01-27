package com.society.leagues.infrastructure.security;

import com.society.leagues.client.api.domain.TokenHeader;
import com.society.leagues.infrastructure.NotAuthorizedResponse;
import com.society.leagues.infrastructure.token.TokenService;
import com.society.leagues.resource.ApiResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;


@Provider
@PreMatching
@Component
public class SecurityFilter implements ContainerRequestFilter {
    static Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    @Autowired TokenService tokenService;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (isLoginRequest(requestContext))
            return;

        if (noToken(requestContext)) {
            requestContext.abortWith(
                    new NotAuthorizedResponse(requestContext).getResponse()
            );
            return;
        }
        requestContext.setSecurityContext(getSecurityContext(requestContext));
    }

    public boolean isLoginRequest(ContainerRequestContext requestContext) {
        return ApiResource.AUTHENTICATE_URL.contains(requestContext.getUriInfo().getPath());
    }

    public boolean noToken(ContainerRequestContext requestContext) {
        return requestContext.getHeaderString(TokenHeader.NAME) == null;
    }

    public SecurityContext getSecurityContext(ContainerRequestContext requestContext) {
        String token = findToken(requestContext);
//        logger.debug("Getting security context for: " + token);
        UserSecurityContext securityContext = tokenService.retrieve(token);
        if (securityContext == null) {
            logger.error("Could not find token in headers or cookies: " + requestContext.getUriInfo());
            logger.error("Token: " + token);
            requestContext.abortWith(new NotAuthorizedResponse(requestContext).getResponse());
            return null;
        }
        return securityContext;
    }

    public String findToken(ContainerRequestContext requestContext) {
        return requestContext.getHeaderString(TokenHeader.NAME);
    }
}
