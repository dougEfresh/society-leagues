package com.society.leagues.infrastructure.security;

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

    @Autowired JdbcTemplate jdbcTemplate;
    Map<String, Object> data;
    Player player;

    @Override
    public AuthenticationWithToken authenticate(String username, String password) {
        //TODO Salt password and use SHA-1
        try {
            data = jdbcTemplate.queryForMap("SELECT * FROM player" +
                                                    " WHERE player_login = ? " +
                                                    "AND `password` = ?",
                                            username,
                                            password
                                           );
            player = new PlayerDb(data);
            logger.info("Successfully logged player_id: " + data.get("player_id"));
        } catch (EmptyResultDataAccessException e) {
            logger.error("No such user: " + username);
            throw new BadCredentialsException("No such user: " + username);
        } catch (Throwable t) {
            logger.error(t.getMessage(),t);
            throw new BadCredentialsException("Unable to verify username " + username);
        }

        AuthenticatedExternalWebService authenticatedExternalWebService = new AuthenticatedExternalWebService(new DomainUser(player), null,
                                                                                                              AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_DOMAIN_USER")
        );

        authenticatedExternalWebService.setExternalServiceAuthenticator(this);

        return authenticatedExternalWebService;
    }
}
