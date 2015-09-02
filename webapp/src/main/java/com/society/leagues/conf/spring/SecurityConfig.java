package com.society.leagues.conf.spring;

import com.allanditzel.springframework.security.web.csrf.CsrfTokenResponseHeaderBindingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.csrf.CsrfFilter;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@SuppressWarnings("unused")
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired LoginHandler loginHandler;
    @Autowired PrincipleDetailsService principleDetailsService;
    @Autowired DataSource datasource;
    @Value("${security-disable:false}")
    boolean securityDisabled = false;

    AuthenticationFailureHandler fHandler = (request, response, exception) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed");

    LogoutSuccessHandler logoutHandler = (request, response, authentication) -> {
        response.setStatus(200);
        response.getWriter().print("ok");
        response.getWriter().flush();
    };

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/health","/css/**", "/img/**", "/js/**", "/login**", "/login.html","/api/users*");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(principleDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

        CsrfTokenResponseHeaderBindingFilter csrfTokenFilter = new CsrfTokenResponseHeaderBindingFilter();
        http.addFilterAfter(csrfTokenFilter, CsrfFilter.class);
        if (securityDisabled) {
            http.csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/**").permitAll()
                    .anyRequest().authenticated().and()
                    .exceptionHandling().authenticationEntryPoint(new AuthenticationEntry("/index.html")).and()
                    .formLogin().permitAll().loginProcessingUrl("/api/authenticate")
                    .usernameParameter("username").passwordParameter("password")
                    .successHandler(loginHandler).failureHandler(fHandler).and()
                    .logout().logoutUrl("/api/logout").logoutSuccessHandler(logoutHandler).and()
                    .rememberMe().key("_spring_security_remember_me").tokenValiditySeconds(86400 * 50)
                    .tokenRepository(tokenRepository());
        } else {
            http.csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/api/authenticate**").permitAll()
                    .antMatchers("/api/reset**").permitAll()
                    .antMatchers("/api/reset/**").permitAll()
                    .antMatchers("/api/logout**").permitAll()
                    .anyRequest().authenticated().and()
                    .exceptionHandling().authenticationEntryPoint(new AuthenticationEntry("/index.html")).and()
                    .formLogin().permitAll().loginProcessingUrl("/api/authenticate")
                    .usernameParameter("username").passwordParameter("password")
                    .successHandler(loginHandler).failureHandler(fHandler).and()
                    .logout().logoutUrl("/api/logout").logoutSuccessHandler(logoutHandler).and()
                    .rememberMe().key("_spring_security_remember_me").tokenValiditySeconds(86400 * 30)
                    .tokenRepository(tokenRepository());
        }
    }

    @Bean
    public JdbcTokenRepositoryImpl tokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setCreateTableOnStartup(false);
        tokenRepository.setDataSource(datasource);
        return tokenRepository;
    }
}
