package com.society.leagues.infrastructure.security;

import com.society.leagues.api.ApiController;
import com.society.leagues.infrastructure.token.TokenAuthenticationProvider;
import com.society.leagues.infrastructure.token.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebMvcSecurity
@EnableWebSecurity
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
    @Autowired
    TokenService tokenService;
    @Autowired SuccessHandler successHandler;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        logger.info("Configure Auth Manager");
        auth.
                authenticationProvider(tokenAuthenticationProvider()).
                authenticationProvider(domainUsernamePasswordAuthenticationProvider());
    }

    /*
    @Bean
    UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter() {
        UsernamePasswordAuthenticationFilter filter = new UsernamePasswordAuthenticationFilter();
        filter.setUsernameParameter("X-Auth-Username");
        filter.setUsernameParameter("X-Auth-Password");
        filter.setPostOnly(false);
        return filter;
    }
    */

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        logger.debug("Configure HttpSecurity");

        //http.authorizeRequests().
//                antMatchers("/api/v**/admin/**").
//                hasAuthority("ROLE_DOMAIN_ADMIN");

        http.
                csrf().disable().
                sessionManagement().
                sessionCreationPolicy(SessionCreationPolicy.STATELESS).
                and().
                authorizeRequests().antMatchers(
                "/api-browser",
                "/api-test",
                "/index.html",
                "/heath").permitAll().and().
                authorizeRequests().
                antMatchers("/api/v**").
                hasRole("DOMAIN_USER").anyRequest().authenticated().
                //hasAuthority("ROLE_DOMAIN_USER").
                and().
                formLogin().usernameParameter("X-Auth-Username").passwordParameter("X-Auth-Password").
                loginPage(ApiController.AUTHENTICATE_URL).successHandler(successHandler).
                failureUrl(ApiController.AUTHENTICATE_URL + "?error").
                permitAll().and().
//                sessionManagement().
  //              sessionAuthenticationStrategy(sessionAuthenticationStrategy()).
    //            and().
                addFilterBefore(
                       new AuthenticationFilter(authenticationManager()),
                       BasicAuthenticationFilter.class
                );
    }


    @Bean
    SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new SessionAuthenticationStrategy() {
            final Logger logger = LoggerFactory.getLogger("SessionAuth");
            @Override
            public void onAuthentication(Authentication authentication, HttpServletRequest request, HttpServletResponse response) throws SessionAuthenticationException {
                logger.debug("SessionAuthStrategy: " + authentication.getDetails());
            }
        };
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
