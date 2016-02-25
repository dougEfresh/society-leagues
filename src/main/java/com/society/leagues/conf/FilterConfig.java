package com.society.leagues.conf;

import com.society.leagues.filter.SessionFilter;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filter = new FilterRegistrationBean();
        filter.setFilter(new SessionFilter());
        filter.addUrlPatterns("/*");
        return filter;
    }
}
