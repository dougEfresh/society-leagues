package com.society.leagues.infrastructure.token;

import com.owlike.genson.Genson;
import com.society.leagues.domain.DomainUser;

import com.society.leagues.infrastructure.security.JdbcServiceAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Store tokens in a DB
 * TODO: Need to implement cache!
 */
@Component
public class TokenServiceJdbc implements TokenService {
    private static final Logger logger = LoggerFactory.getLogger(TokenServiceJdbc.class);
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired JdbcServiceAuthenticator serviceAuth;

    //private static final Cache restApiAuthTokenCache = CacheManager.getInstance().getCache("restApiAuthTokenCache");
    //TODO use ecache?
    private static final HashMap<String,String> restApiAuthTokenCache = new HashMap<>();
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
    public void store(String token, String authentication) {
        //if (authentication.getPrincipal() == null)
          ///  throw new RuntimeException("Principal is null");

        //logger.debug(String.format("Store token: %s user: %s", token,
          //      authentication.getPrincipal().toString()));

        restApiAuthTokenCache.put(token,authentication);

        //DomainUser user = (DomainUser) authentication.getPrincipal();
        /*user.setAuthenticated(true);
        String commaListAuthorities = "";
        for(GrantedAuthority authority: authentication.getAuthorities()) {
            commaListAuthorities += authority.getAuthority() + ",";
        }

        user.setAuthorities(commaListAuthorities.substring(0,commaListAuthorities.length()-1));
        user.setToken(token);
        String json = new Genson().serialize(user);

        logger.debug("Storing " + json + " to db");
        //TODO Check for existing token?
        jdbcTemplate.update("insert into token_cache (token,player) VALUES (?,?)",
                token,
                json);
                */
    }

    @Override
    public boolean contains(String token) {
        logger.debug("Checking token: " + token);

        if (restApiAuthTokenCache.containsKey(token))
            return true;

        try {
            String player = jdbcTemplate.queryForObject("SELECT player FROM token_cache WHERE token = ?", String.class, token);
            if (player == null || player.isEmpty()) {
                logger.debug("Could not find player for token: " + token);
                return false;
            }
            restApiAuthTokenCache.put(token,"");
            return true;
        } catch (EmptyResultDataAccessException e) {
            logger.debug("Could not find player for token: " + token);
        }
        return false;
    }

    @Override
    public String retrieve(String token) {
        return token;
        /*
        if (restApiAuthTokenCache.containsKey(token))
            return restApiAuthTokenCache.get(token);

        String player;
        try {
            player = jdbcTemplate.queryForObject("SELECT player FROM token_cache WHERE token = ?", String.class, token);
        } catch (EmptyResultDataAccessException e) {
            //TODO: Just throw NULL back?
            throw new RuntimeException("Could not authorized " + token);
        }
        return restApiAuthTokenCache.put(token,deserialize(player));
        */
    }

    /*
    @SuppressWarnings("unchecked")
    public AuthenticationWithToken deserialize(String user) {
        logger.info("Deserialize player: " + user);
        DomainUser domainUser = new Genson().deserialize(user, DomainUser.class);
        AuthenticatedExternalWebService auth  =
                new AuthenticatedExternalWebService(domainUser,
                        null,
                        AuthorityUtils.commaSeparatedStringToAuthorityList(domainUser.getAuthorities())
                );

        auth.setDetails(domainUser.getToken());
        auth.setAuthenticated(domainUser.isAuthenticated());
        auth.setExternalServiceAuthenticator(serviceAuth);

        return auth;
    }
    */
}
