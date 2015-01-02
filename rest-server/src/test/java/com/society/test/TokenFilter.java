package com.society.test;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import java.io.IOException;

public class TokenFilter  implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        String token = (String) requestContext.getProperty(TestBase.X_AUTH_TOKEN);
        if (token == null || token.isEmpty())
            return;
        requestContext.getHeaders().add(TestBase.X_AUTH_TOKEN,token);
    }
}
