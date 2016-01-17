package com.society.interceptors;

import com.society.leagues.security.CookieContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CookieInterceptor extends HandlerInterceptorAdapter {

    static {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context instanceof CookieContext) {
            CookieContext cookieContext = (CookieContext) context;
            if (cookieContext.isCookieChange())
                response.addHeader("Set-Cookie:",cookieContext.getNewCookies().toString().replace("[","").replace("]",""));
        }
        super.postHandle(request, response, handler, modelAndView);
    }
}
