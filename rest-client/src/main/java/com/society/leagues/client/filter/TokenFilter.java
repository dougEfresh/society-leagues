package com.society.leagues.client.filter;

import com.society.leagues.client.api.domain.TokenHeader;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import java.io.IOException;

public class TokenFilter implements ClientRequestFilter {
    final String token;

    public TokenFilter(String token) {
        this.token = token;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (token == null || token.isEmpty())
            return;
        requestContext.getHeaders().add(TokenHeader.NAME,token);
    }
}
