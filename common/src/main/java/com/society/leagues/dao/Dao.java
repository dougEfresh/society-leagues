package com.society.leagues.dao;

import com.society.leagues.client.api.ClientApi;
import com.society.leagues.client.api.domain.LeagueObject;
import com.society.leagues.infrastructure.LeagueCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Dao<Q extends LeagueObject> implements ClientApi<Q> {

    @Autowired JdbcTemplate jdbcTemplate;
    static Logger logger = LoggerFactory.getLogger(Dao.class);
    protected LeagueCache<Q> cache = new LeagueCache<>();

    /**
     * The sql to get the LeagueObject Q
     * @return String
     */
    public abstract String getSql();

    /**
     * To map the rows for LeagueObject Q
     * @return
     */
    public abstract RowMapper<Q> getRowMapper();

    public Q get(Integer id) {
        Q obj = cache.get(id);
        if (obj != null)
            return obj;

        refreshCache();
        return cache.get(id);
    }

    @Override
    public Collection<Q> get() {
        if (cache.isEmpty()) {
            refreshCache();
        }
        return cache.get();
    }

    @Override
    public List<Q> get(List<Integer> id) {
        return get().stream().filter(o -> id.contains(o.getId())).collect(Collectors.toList());
    }

    public List<Q> list(String sql, Object ...args) {
        try {
            return jdbcTemplate.query(sql, getRowMapper(),args);
        } catch (Throwable t){
            logger.error(t.getLocalizedMessage(),t);
        }
        return null;
    }

    public Q create(Q thing, PreparedStatementCreator st) {
        try {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(st,keyHolder);
            thing.setId(keyHolder.getKey().intValue());
            refreshCache();
        } catch (Throwable t) {
            logger.error(t.getMessage(),t);
            return null;
        }
        return thing;
    }

    public Boolean delete(Q thing, String sql) {
        try {
            Boolean returned = jdbcTemplate.update(sql, thing.getId()) > 0;
            refreshCache();
            return returned;
        } catch (Throwable t) {
            logger.error(t.getMessage(),t);
        }
        return Boolean.FALSE;
    }

    public <T extends LeagueObject> T modify(T thing, String sql, Object ...args) {
        try {
            if (jdbcTemplate.update(sql,args) <= 0)
                return null;
            refreshCache();
            return thing;
        } catch (Throwable t) {
            logger.error(t.getMessage(),t);
        }
        return null;
    }

    static final int HALF_AN_HOUR_IN_MILLISECONDS = 30 * 60 * 1000;
    @Scheduled(fixedRate = HALF_AN_HOUR_IN_MILLISECONDS)
    protected synchronized void refreshCache() {
        cache.clear();
        cache.set(list(getSql()));
    }
}
