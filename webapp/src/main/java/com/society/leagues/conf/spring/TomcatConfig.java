package com.society.leagues.conf.spring;

import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Bean
    EmbeddedServletContainerCustomizer servletContainerCustomizer() {
        return new SessionConfig();
    }
}
