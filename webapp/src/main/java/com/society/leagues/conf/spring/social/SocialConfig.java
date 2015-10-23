package com.society.leagues.conf.spring.social;

import com.society.leagues.conf.spring.PrincipleDetailsService;
import com.society.leagues.conf.spring.social.mongo.MongoSocialUsersDetailService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.social.connect.*;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.*;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;

import javax.sql.DataSource;

@Configuration
public class SocialConfig  {
    final static Logger logger = Logger.getLogger(SocialConfig.class);
    @Autowired MongoOperations mongoOperations;
    @Autowired Environment environment;
    @Autowired DataSource dataSource;
    @Autowired PrincipleDetailsService principleDetailsService;
    @Autowired ImplicitSignInAdapter signup;
    @Autowired UsersConnectionRepository usersConnectionRepository;
    @Value("${app.url}")
    String appUrl;
    @Autowired ConnectionFactoryLocator connectionFactoryLocator;

    @Bean
    public SocialUserDetailsService socialUsersDetailService() {
        return new MongoSocialUsersDetailService(principleDetailsService);
    }

    @Bean
    public FacebookConnectionFactory facebookConnectionFactory() {
        return new FacebookConnectionFactory(
                environment.getProperty("spring.social.facebook.app-id"),
                environment.getProperty("spring.social.facebook.app-secret"));
    }

    @Bean
    @Scope(value="singleton", proxyMode= ScopedProxyMode.INTERFACES)
    public ConnectionFactoryLocator connectionFactoryLocator() {
        ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
        registry.addConnectionFactory(facebookConnectionFactory());
        return registry;
    }

    @Bean
    public ProviderSignInController providerSignInController() {
        ProviderSignInController controller = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository,signup);
        controller.setApplicationUrl(appUrl);
        controller.setSignInUrl(appUrl + "/#/fb/signin");
        controller.setSignUpUrl(appUrl + "/#/fb/signup");
        controller.setPostSignInUrl(appUrl + "/#/app/home");
        controller.addSignInInterceptor(new ProviderSignInInterceptor<Object>() {
            @Override
            public void preSignIn(ConnectionFactory<Object> connectionFactory, MultiValueMap<String, String> parameters, WebRequest request) {
                logger.info("Got PreSignIn ");
            }

            @Override
            public void postSignIn(Connection<Object> connection, WebRequest request) {
                logger.info("Got postSignIn ");
            }
        });
        return controller;
    }

    @Bean
    public ProviderSignInUtils providerSignInUtils() {
        return new ProviderSignInUtils(connectionFactoryLocator,usersConnectionRepository);
    }

}
