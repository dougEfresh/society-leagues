package com.society.leagues.conf.spring.social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;


@Configuration
public class ConnectConfig   {
    @Autowired UsersConnectionRepository usersConnectionRepository;

    @Bean
    public ConnectController connectController(ConnectionFactoryLocator connectionFactoryLocator,
        ConnectionRepository connectionRepository) {
        ConnectController controller = new ConnectController(connectionFactoryLocator, connectionRepository);
        controller.setApplicationUrl("http://leaguesdev.societybilliards.com:8080") ;
        return controller;
    }

    @Bean
    @Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)
    public ConnectionRepository connectionRepository() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("Unable to get a ConnectionRepository: no user signed in");
        }
        return usersConnectionRepository.createConnectionRepository(authentication.getName());
    }

}
