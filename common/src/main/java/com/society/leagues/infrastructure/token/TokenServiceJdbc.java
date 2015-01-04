package com.society.leagues.infrastructure.token;

import com.owlike.genson.Genson;
import com.society.leagues.infrastructure.security.JdbcServiceAuthenticator;
import com.society.leagues.infrastructure.security.UserSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Store tokens in a DB
 * TODO: Need to implement cache
 */
@Component
public class TokenServiceJdbc implements TokenService {
    private static final Logger logger = LoggerFactory.getLogger(TokenServiceJdbc.class);
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired JdbcServiceAuthenticator serviceAuth;

    private static final AtomicReference<HashMap<String,UserSecurityContext>>
            cache = new AtomicReference<>(new HashMap<>());

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
            cache.get().remove(oldToken);
        }
        int dropped = jdbcTemplate.update("delete from token_cache where created_date  < ADDDATE(now(),-30)" );
        logger.info("Dropped " + dropped + " tokens");
    }

    @Override
    public String generateNewToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void store(String token, UserSecurityContext securityContext) {
        cache.get().put(token,securityContext);
        String json =  new Genson().serialize(securityContext);
        logger.debug("Storing " + json + " to db");
        //TODO Check for existing token?
        jdbcTemplate.update("REPLACE into token_cache (token,player) VALUES (?,?)",
                token,
                json);
    }

    @Override
    public boolean contains(String token) {
        logger.debug("Checking token: " + token);

        if (cache.get().containsKey(token))
            return true;

        try {
            String json = jdbcTemplate.queryForObject("SELECT player FROM token_cache WHERE token = ?", String.class, token);
            if (json == null || json.isEmpty()) {
                logger.warn("Could not find player for token: " + token);
                return false;
            }
            UserSecurityContext context = new Genson().deserialize(json,UserSecurityContext.class);
            cache.get().put(token,context);
            return true;
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Could not find player for token: " + token);
        }
        return false;
    }

    @Override
    public UserSecurityContext retrieve(String token) {

        if (cache.get().containsKey(token))
            return cache.get().get(token);

         try {
            String json = jdbcTemplate.queryForObject("SELECT player FROM token_cache WHERE token = ?", String.class, token);
            if (json == null || json.isEmpty()) {
                logger.warn("Could not find player for token: " + token);
                return null;
            }
            UserSecurityContext context = new Genson().deserialize(json,UserSecurityContext.class);
            cache.get().put(token,context);
            return context;
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Could not find player for token: " + token);
        }

        return null;
    }

}
