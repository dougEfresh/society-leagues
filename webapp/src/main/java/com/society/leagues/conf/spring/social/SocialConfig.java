package com.society.leagues.conf.spring.social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;

import javax.sql.DataSource;

@Configuration
public class SocialConfig extends SocialConfigurerAdapter {

    @Autowired MongoOperations mongoOperations;

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        MongoConnectionTransformers transformers = new MongoConnectionTransformers(connectionFactoryLocator, Encryptors.noOpText());
        MongoUsersConnectionRepository repo = new MongoUsersConnectionRepository(mongoOperations, connectionFactoryLocator, transformers);
        repo.setConnectionSignUp(new ImplicitConnectionSignup());
        return repo;
    }

    /*
    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer cfConfig, Environment env) {
        cfConfig.addConnectionFactory(new FacebookConnectionFactory(
                env.getProperty("spring.social.facebook.app-id"),
                env.getProperty("spring.social.facebook.app-secret")));
    }
*/

    private static class ImplicitConnectionSignup implements ConnectionSignUp {
        @Override
        public String execute(Connection<?> connection) {
            return connection.getKey().getProviderUserId();
        }
    }
}

    //@Bean
    //public SignInAdapter signInAdapter() {
      //  return new ImplicitSignInAdapter(new HttpSessionRequestCache());
    //}
