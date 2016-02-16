package com.society.leagues.conf;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static com.stormpath.spring.config.StormpathWebSecurityConfigurer.stormpath;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

      @Override
    public void configure(HttpSecurity http) throws Exception {
        http.apply(stormpath()).and().authorizeRequests().antMatchers("/*").permitAll();
    }
}
