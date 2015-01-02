package com.society.leagues.infrastructure.security;
import com.society.leagues.infrastructure.NotAuthorizedResponse;
import com.society.leagues.resource.ApiResource;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.io.IOException;


@Provider
@PreMatching
public class SecurityFilter implements ContainerRequestFilter {

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
    }

    public boolean isLoginRequest(ContainerRequestContext requestContext) {
        return ApiResource.AUTHENTICATE_URL.contains(requestContext.getUriInfo().getPath());
    }

    public boolean noToken(ContainerRequestContext requestContext) {
        return requestContext.getHeaderString(ApiResource.X_AUTH_TOKEN) == null;
    }
}
