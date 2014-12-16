package com.society.leagues.infrastructure.security;

import com.society.leagues.domain.DomainUser;
import com.society.leagues.domain.interfaces.Player;
import com.society.leagues.infrastructure.AuthenticatedExternalWebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebMvcSecurity
@EnableScheduling
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    static Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(
                domainUsernamePasswordAuthenticationProvider()).
                authenticationProvider(tokenAuthenticationProvider());
    }

    @Autowired ExternalServiceAuthenticator serviceAuthenticator;
    @Autowired TokenService tokenService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        logger.debug("Configure HttpSecurity");
        http.
                csrf().disable().
                sessionManagement().
                sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests().
                antMatchers("/api/v**").
                hasAuthority("ROLE_DOMAIN_USER");

        http.authorizeRequests().antMatchers(
                "/api-browser/**",
                "/api-test/**",
                "/index.html",
                "/heath").permitAll();

        http.addFilterBefore(
                new AuthenticationFilter(authenticationManager()),
                BasicAuthenticationFilter.class
        );

    }

    @Bean(name = "tokenAuthenticationProvider")
    public AuthenticationProvider tokenAuthenticationProvider() {
        return new TokenAuthenticationProvider();
    }

    @Bean
    public AuthenticationProvider domainUsernamePasswordAuthenticationProvider() {
        return new DomainUsernamePasswordAuthenticationProvider(tokenService, serviceAuthenticator);
    }

    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
