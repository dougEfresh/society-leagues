package com.society.leagues.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.society.leagues.infrastructure.token.TokenResponse;
import com.society.leagues.infrastructure.token.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SuccessHandler implements AuthenticationSuccessHandler {
    Logger logger = LoggerFactory.getLogger(SuccessHandler.class);
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired
    TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        logger.info("onAuthSuccess");
        String token = tokenService.generateNewToken();
        tokenService.store(token,authentication);

        response.setStatus(HttpServletResponse.SC_OK);
        TokenResponse tokenResponse = new TokenResponse(token);
        String tokenJsonResponse = new ObjectMapper().writeValueAsString(tokenResponse);
        response.addHeader("Content-Type", "application/json");
        response.getWriter().print(tokenJsonResponse);
    }
}
