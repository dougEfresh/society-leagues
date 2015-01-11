package com.society.leagues.infrastructure.token;

import com.owlike.genson.Genson;
import com.society.leagues.infrastructure.security.JdbcServiceAuthenticator;
import com.society.leagues.infrastructure.security.UserSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Store tokens in a DB
 * TODO: Need to implement cache
 */
@Component
@SuppressWarnings("unused")
public class TokenServiceJdbc implements TokenService {
    private static final Logger logger = LoggerFactory.getLogger(TokenServiceJdbc.class);
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired JdbcServiceAuthenticator serviceAuth;

    private static final AtomicReference<HashMap<String,UserSecurityContext>>
            cache = new AtomicReference<>(new HashMap<>());

    public static final int HALF_AN_HOUR_IN_MILLISECONDS = 30 * 60 * 1000;

    @Override
    public void clearCache() {
        cache.get().clear();
    }

    @Override
    @Scheduled(fixedRate = HALF_AN_HOUR_IN_MILLISECONDS)
    public void evictExpiredTokens() {
        logger.info("Evicting expired tokens");
        Date expiredDate = Date.from(new Date().toInstant().minus(30, ChronoUnit.DAYS));
        List<String> oldTokens = jdbcTemplate.queryForList(
                "select token from token_cache where created_date  < ?",
                String.class,
                expiredDate);
        for (String oldToken : oldTokens) {
            cache.get().remove(oldToken);
        }
        int dropped = jdbcTemplate.update("delete from token_cache where created_date < ?", expiredDate );
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
        jdbcTemplate.update("INSERT into token_cache (token,player) VALUES (?,?)", token, json);
    }

    @Override
    public boolean contains(String token) {
        logger.debug("Checking token: " + token);

        if (cache.get().containsKey(token))
            return true;

        try {
            UserSecurityContext context = new Genson().deserialize(getJson(token),UserSecurityContext.class);
            cache.get().put(token,context);
            return true;
        } catch (Throwable t) { logger.error(t.getMessage(), t); }
        return false;
    }

    @Override
    public UserSecurityContext retrieve(String token) {

        if (cache.get().containsKey(token))
            return cache.get().get(token);

         try {
            UserSecurityContext context = new Genson().deserialize(getJson(token),UserSecurityContext.class);
            cache.get().put(token,context);
            return context;
        } catch (Throwable t) { logger.error(t.getMessage(), t); }

        return null;
    }

    private String getJson(String token) {
        try {
            String json = jdbcTemplate.queryForObject("SELECT player FROM token_cache WHERE token = ?", String.class, token);
            if (json == null || json.isEmpty()) {
                throw new RuntimeException("No token: " + token );
            }
            return json;
        } catch (Throwable t) {
            logger.warn("Could not find player for token: " + token);
            logger.error(t.getMessage(), t);
        }
        return null;
    }
}
