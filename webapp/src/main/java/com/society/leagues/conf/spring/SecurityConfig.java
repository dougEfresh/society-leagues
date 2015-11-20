package com.society.leagues.conf.spring;

import com.allanditzel.springframework.security.web.csrf.CsrfTokenResponseHeaderBindingFilter;
import com.society.leagues.persistence.MongoTokenRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.social.UserIdSource;
import org.springframework.social.security.SpringSocialConfigurer;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@SuppressWarnings("unused")
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired LoginHandler loginHandler;
    @Autowired PrincipleDetailsService principleDetailsService;
    @Value("${security-disable:false}")
    boolean securityDisabled = false;
    @Autowired MongoOperations mongo;
    @Autowired AuthenticationManager authenticationManager;
    @Autowired UserIdSource userIdSource;
    @Autowired DataSource dataSource;

    AuthenticationFailureHandler fHandler = (request, response, exception) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed");

    LogoutSuccessHandler logoutHandler = (request, response, authentication) -> {
        response.setStatus(200);
        response.getWriter().print("ok");
        response.getWriter().flush();
    };

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/health",
                "/css/**", "/img/**",
                "/*.js", "/index.html", "/lang/**","/fonts/**","/lib/**", "/images/**",
                "/js/**",
                 "/api-docs**",
                "/api-docs/**", "/api/sheets/**","/api/user/reset/**",
                "/mappings"
        );
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(principleDetailsService).passwordEncoder(new BCryptPasswordEncoder());
        auth.authenticationProvider(rememberMeAuthenticationProvider());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

        CsrfTokenResponseHeaderBindingFilter csrfTokenFilter = new CsrfTokenResponseHeaderBindingFilter();
        http.addFilterAfter(csrfTokenFilter, CsrfFilter.class);
        http.csrf().disable()
                    .formLogin().loginPage("/api/authenticate")
                    //.loginProcessingUrl("/signup/authenticate")
                    .failureUrl("/")
                    .usernameParameter("username").passwordParameter("password").successHandler(loginHandler).failureHandler(fHandler)
                    .and().logout().logoutUrl("/api/logout").logoutSuccessHandler(logoutHandler).and()
                    .authorizeRequests().antMatchers(
                "/api/sheets/**","/signin/**","/api/signup","/api/authenticate","/api/logout","/api/user/reset/**").permitAll()
                    .antMatchers("/**").authenticated().and()
                    .exceptionHandling().authenticationEntryPoint(new AuthenticationEntry("/index.html")).and()
                    //.formLogin().permitAll().loginProcessingUrl("/api/authenticate").usernameParameter("username").passwordParameter("password").successHandler(loginHandler).failureHandler(fHandler).and()
                    .logout().logoutUrl("/api/logout").logoutSuccessHandler(logoutHandler).and().rememberMe()
                    .rememberMeServices(rememberMeServices()).and()
                    .apply(new SpringSocialConfigurer())
                    .postLoginUrl("/app/home").alwaysUsePostLoginUrl(false)
                    //.signupUrl("/signup").defaultFailureUrl("/failure")
                    .userIdSource(userIdSource);
    }

    @Bean
    RememberMeAuthenticationProvider rememberMeAuthenticationProvider() {
        return new CustomAuthenticationProvider("springRememberMe");
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    @Bean
    RememberMeServices rememberMeServices() {
        PersistentTokenBasedRememberMeServices rememberMeServices =
                new PersistentTokenBasedRememberMeServices("springRememberMe",principleDetailsService,tokenRepository());
        rememberMeServices.setTokenValiditySeconds(86400 * 90);
        rememberMeServices.setAlwaysRemember(true);
        return rememberMeServices;
    }

    @Bean
    public TextEncryptor textEncryptor() {
        return Encryptors.noOpText();
    }

  }
