package com.society.leagues.infrastructure.security;

import com.society.leagues.dao.PlayerDao;
import com.society.leagues.domain.interfaces.Player;
import com.society.leagues.infrastructure.token.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

@Component
public class JdbcServiceAuthenticator implements ServiceAuthenticator {
    private final static Logger logger = LoggerFactory.getLogger(JdbcServiceAuthenticator.class);
    @Autowired PlayerDao playerDao;
    @Autowired TokenService tokenService;

    @Override
    public PrincipalToken authenticate(String username, String password) {
        Player player;
        try {
            player = playerDao.getPlayer(username,password);
            logger.info("Successfully logged player_id: " + player.getId());
        } catch (EmptyResultDataAccessException e) {
            logger.error("No such user: " + username);
            throw new RuntimeException("No such user: " + username);
        } catch (Throwable t) {
            logger.error(t.getMessage(),t);
            throw new RuntimeException("Unable to verify username " + username);
        }
        PrincipalToken principalToken = new PrincipalToken(
                tokenService.generateNewToken(),
                player.getId() + ""
                );

        principalToken.addRole("USER");
        if (player.isAdmin())
            principalToken.addRole("ADMIN");

        return principalToken;
    }
}
