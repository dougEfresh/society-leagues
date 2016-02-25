package com.society.leagues.filter;

import com.society.leagues.security.CookieContext;
import org.apache.catalina.connector.RequestFacade;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.*;
import java.io.IOException;

public class SessionFilter implements Filter {
    private static Logger logger = Logger.getLogger(SessionFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        RequestFacade requestFacade = (RequestFacade) request;
        String cookies = requestFacade.getHeader("Cookie");
        SecurityContext context = new CookieContext(cookies);
        SecurityContextHolder.setContext(context);
        chain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}
