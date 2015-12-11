package com.society.admin;


import com.society.admin.security.CookieAuth;
import com.society.admin.security.CookieContext;
import feign.Client;
import feign.Request;
import feign.Response;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;

public class LeagueHttpClient extends Client.Default {

    public LeagueHttpClient() {
        super(null,null);
    }

    public LeagueHttpClient(SSLSocketFactory sslContextFactory, HostnameVerifier hostnameVerifier) {
        super(sslContextFactory, hostnameVerifier);
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        Response response = super.execute(request, options);
        if (response.headers().containsKey("Set-Cookie")) {
            SecurityContext context = SecurityContextHolder.getContext();
            if (context instanceof CookieContext) {
                CookieContext cookieContext = (CookieContext) context;
                cookieContext.setCookieChange(true);
                cookieContext.setNewCookies(response.headers().get("Set-Cookie"));
            }
        }
        return response;
    }
}
