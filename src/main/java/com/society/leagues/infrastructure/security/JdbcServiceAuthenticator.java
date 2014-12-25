package com.society.leagues.infrastructure.security;

import com.society.leagues.api.player.PlayerDao;
import com.society.leagues.domain.DomainUser;
import com.society.leagues.domain.player.PlayerDb;
import com.society.leagues.domain.interfaces.Player;
import com.society.leagues.infrastructure.AuthenticatedExternalWebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JdbcServiceAuthenticator implements ExternalServiceAuthenticator {
    private final static Logger logger = LoggerFactory.getLogger(JdbcServiceAuthenticator.class);
    @Autowired
    PlayerDao playerDao;

    @Override
    public AuthenticationWithToken authenticate(String username, String password) {
        Player player;
        try {
            player = playerDao.getPlayer(username,password);
            logger.info("Successfully logged player_id: " + player.getId());
        } catch (EmptyResultDataAccessException e) {
            logger.error("No such user: " + username);
            throw new BadCredentialsException("No such user: " + username);
        } catch (Throwable t) {
            logger.error(t.getMessage(),t);
            throw new BadCredentialsException("Unable to verify username " + username);
        }

        String authorityList = "ROLE_DOMAIN_USER";
        if (player.isAdmin())
            authorityList += authorityList + ",ROLE_DOMAIN_ADMIN";

        AuthenticatedExternalWebService authenticatedExternalWebService =
                new AuthenticatedExternalWebService(
                new DomainUser(player),
                password,
                AuthorityUtils.commaSeparatedStringToAuthorityList(authorityList)
        );

        authenticatedExternalWebService.setExternalServiceAuthenticator(this);

        return authenticatedExternalWebService;
    }
}
