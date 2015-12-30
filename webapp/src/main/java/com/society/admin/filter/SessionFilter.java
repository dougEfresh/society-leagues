package com.society.admin.filter;

import com.society.admin.security.CookieContext;
import org.apache.catalina.connector.RequestFacade;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.*;
import java.io.IOException;

public class SessionFilter implements Filter {

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
