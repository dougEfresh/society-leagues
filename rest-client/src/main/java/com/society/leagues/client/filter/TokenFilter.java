package com.society.leagues.client.filter;

import com.society.leagues.client.api.domain.TokenHeader;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import java.io.IOException;

public class TokenFilter implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        String token = (String) requestContext.getProperty(TokenHeader.NAME);
        if (token == null || token.isEmpty())
            return;
        requestContext.getHeaders().add(TokenHeader.NAME,token);
    }
}
