package com.society.leagues.conf.spring;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class SessionConfig  {

    @Bean
    public ServletContextInitializer servletContextInitializer() {
    return new ServletContextInitializer() {
        @Override
        public void onStartup(ServletContext servletContext) throws ServletException {
            servletContext.getSessionCookieConfig().setMaxAge(Integer.MAX_VALUE/2);
        }
    };

}

}
