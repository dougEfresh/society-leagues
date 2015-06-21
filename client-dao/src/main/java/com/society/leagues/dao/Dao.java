package com.society.leagues.dao;

import com.society.leagues.client.api.ClientApi;
import com.society.leagues.client.api.domain.LeagueObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Dao<Q extends LeagueObject> implements ClientApi<Q> {
    static Logger logger = LoggerFactory.getLogger(Dao.class);
    @Autowired JdbcTemplate jdbcTemplate;
    protected LeagueCache<Q> cache;

    @PostConstruct
    public void init() {
        cache = new LeagueCache<>();
    }

    public abstract String getIdName();
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
        if (cache.isEmpty()) {
            refreshCache();
        }
        return cache.get(id);
    }

    private Q getNoCache(Integer id) {
        return jdbcTemplate.queryForObject(getSql() + " where " + getIdName() + " = ?", getRowMapper(), id);
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

    private List<Q> list(String sql, Object ...args) {
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
            Q n = getNoCache(keyHolder.getKey().intValue());
            cache.add(n);
            return n;
        } catch (Throwable t) {
            logger.error(t.getMessage(),t);
            return null;
        }
    }

    public Boolean delete(Q thing, String sql) {
        try {
            Boolean returned = jdbcTemplate.update(sql, thing.getId()) > 0;
            cache.remove(thing);
            return returned;
        } catch (Throwable t) {
            logger.error(t.getMessage(),t);
        }
        return Boolean.FALSE;
    }

    public Q modify(Q thing, String sql, Object ...args) {
        try {
            if (jdbcTemplate.update(sql,args) <= 0)
                return null;

            cache.modify(thing.getId(),getNoCache(thing.getId()));
            return cache.get(thing.getId());
        } catch (Throwable t) {
            logger.error(t.getMessage(),t);
        }
        return null;
    }

    protected synchronized void refreshCache() {
        logger.info("Refreshing cache " + this.getClass().getCanonicalName());
        cache.set(list(getSql()));
    }
}
