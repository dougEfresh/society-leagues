package com.society.leagues.infrastructure.security;

import com.society.leagues.client.api.domain.User;
import com.society.leagues.dao.AuthDao;
import com.society.leagues.infrastructure.token.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

@Component
public class JdbcServiceAuthenticator implements ServiceAuthenticator {
    private final static Logger logger = LoggerFactory.getLogger(JdbcServiceAuthenticator.class);
    @Autowired AuthDao dao;

    @Override
    public User authenticate(String username, String password) {
        User user = null;
        try {
            user = dao.getUser(username, password);
            logger.info("Successfully logged player_id: " +user.getId());
        } catch (EmptyResultDataAccessException e) {
            logger.error("No such user: " + username);
        } catch (Throwable t) {
            logger.error(t.getMessage(),t);
        }
        return user;
    }
}
