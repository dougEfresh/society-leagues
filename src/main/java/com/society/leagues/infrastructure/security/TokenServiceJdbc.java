package com.society.leagues.infrastructure.security;

import com.owlike.genson.Genson;
import com.society.leagues.domain.DomainUser;
import org.omg.SendingContext.RunTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Store tokens in a DB
 * TODO: Need to implement cache!
 */
@Component
public class TokenServiceJdbc implements TokenService {
    private static final Logger logger = LoggerFactory.getLogger(TokenServiceJdbc.class);
    @Autowired JdbcTemplate jdbcTemplate;
    //private static final Cache restApiAuthTokenCache = CacheManager.getInstance().getCache("restApiAuthTokenCache");
    //TODO use ecache?
    private static final HashMap<String,Authentication> restApiAuthTokenCache = new HashMap<>();
    public static final int HALF_AN_HOUR_IN_MILLISECONDS = 30 * 60 * 1000;

    @PostConstruct
    public void init() {
        jdbcTemplate.execute("create table IF NOT EXISTS token_cache(token varchar(64)," +
                        " player varchar(4096) NOT NULL," +
                " created_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                " PRIMARY KEY (token))"
        );
    }

    @Override
    @Scheduled(fixedRate = HALF_AN_HOUR_IN_MILLISECONDS)
    public void evictExpiredTokens() {
        logger.info("Evicting expired tokens");
        List<String> oldTokens = jdbcTemplate.queryForList("select token from token_cache where created_date  < ADDDATE(now(),-30)",
                String.class);
        for (String oldToken : oldTokens) {
            restApiAuthTokenCache.remove(oldToken);
        }
        int dropped = jdbcTemplate.update("delete from token_cache where created_date  < ADDDATE(now(),-30)" );
        logger.info("Dropped " + dropped + " tokens");
    }

    @Override
    public String generateNewToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void store(String token, Authentication authentication) {
        if (authentication.getPrincipal() == null)
            throw new RuntimeException("Principal is null");

        logger.debug(String.format("Store token: %s user: %s", token,
                authentication.getPrincipal().toString()));

        restApiAuthTokenCache.put(token,authentication);

        //String json = new Genson().serialize( ((DomainUser) authentication.getPrincipal()).getPlayer());
        String json = new Genson().serialize(authentication);

        logger.debug("Storing " + json + " to db");
        //TODO Check for existing token?
        jdbcTemplate.update("insert into token_cache (token,player) VALUES (?,?)",
                token,
                json);
    }

    @Override
    public boolean contains(String token) {
        logger.debug("Checking token: " + token);

        if (restApiAuthTokenCache.containsKey(token))
            return true;

        try {
            String player = jdbcTemplate.queryForObject("SELECT player FROM token_cache WHERE token = ?", String.class, token);
            if (player == null || player.isEmpty())
                return false;

            AuthenticationWithToken auth = new Genson().deserialize(player, AuthenticationWithToken.class);
            restApiAuthTokenCache.put(token, auth);
            return true;
        } catch (EmptyResultDataAccessException e) {

        }
        return false;
    }

    @Override
    public Authentication retrieve(String token) {
        if (restApiAuthTokenCache.containsKey(token))
            return restApiAuthTokenCache.get(token);

        String player;
        try {
            player = jdbcTemplate.queryForObject("SELECT player FROM token_cache WHERE token = ?", String.class, token);
        } catch (EmptyResultDataAccessException e) {
            //TODO: Just throw NULL back?
            throw new RuntimeException("Could not authorized " + token);
        }

        AuthenticationWithToken auth = new Genson().deserialize(player,AuthenticationWithToken.class);
        return restApiAuthTokenCache.put(token,auth);
    }

}
