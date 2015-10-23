package com.society.leagues.conf.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.mongo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginHandler implements AuthenticationSuccessHandler {
    @Autowired UserRepository userRepository;
    @Autowired ObjectMapper mapper;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        org.springframework.security.core.userdetails.User springUser = (org.springframework.security.core.userdetails.User)authentication.getPrincipal();
        response.setContentType("application/json;charset=UTF-8");
        //response.getWriter().flush();
        User u = userRepository.findByLogin(springUser.getUsername());
        mapper.writer().writeValue(response.getWriter(),u);
    }
}
