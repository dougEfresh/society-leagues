package com.society.leagues.dao;

import com.society.leagues.client.api.domain.LeagueObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class Dao {
    private static Logger logger = LoggerFactory.getLogger(Dao.class);
    @Autowired public JdbcTemplate jdbcTemplate;

    public <T extends LeagueObject> T create(T thing, PreparedStatementCreator st) {
        try {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(st,keyHolder);
            thing.setId(keyHolder.getKey().intValue());
        } catch (Throwable t) {
            logger.error(t.getMessage(),t);
            return null;
        }
        return thing;
    }

    public <T extends LeagueObject> Boolean delete(T thing, String sql) {
          try {
            return jdbcTemplate.update(sql,thing.getId()) > 0;
        } catch (Throwable t) {
            logger.error(t.getMessage(),t);
        }
        return Boolean.FALSE;
    }

    public <T extends LeagueObject> T modify(T thing, String sql, Object ...args) {
        try {
            if (jdbcTemplate.update(sql,args) <= 0)
                return null;

            return thing;
        } catch (Throwable t) {
            logger.error(t.getMessage(),t);
        }
        return null;
    }

    public <T extends LeagueObject> T get(String sql, Object[] args, RowMapper<T> rowMapper) {
        try {
            return jdbcTemplate.queryForObject(sql,args,rowMapper);
        } catch (Throwable t) {
            logger.error(t.getLocalizedMessage(),t);
        }
        return null;
    }
    
    public <T extends LeagueObject> T get(String sql, Object id, RowMapper<T> rowMapper) {
        try {
            return jdbcTemplate.queryForObject(sql,new Object[] {id},rowMapper);
        } catch (Throwable t) {
            logger.error(t.getLocalizedMessage(),t);
        }
        return null;
    }
    
    public List<Map<String,Object>> get(String sql, Object ...args){
        try {
            return jdbcTemplate.queryForList(sql, args);
        } catch (Throwable t) {
            logger.error(t.getLocalizedMessage(),t);
        }
        return null;
    }
    
    public <T extends LeagueObject> T get(String sql,RowMapper<T> rowMapper,Object ...args) {
        try {
            List<T> list = jdbcTemplate.query(sql, rowMapper, args);
            return list.get(0);
        } catch (Throwable t) {
            logger.error(t.getLocalizedMessage(),t);
        }
        return null;
    }
    
    public Integer getId(String table, LeagueObject leagueObject) {
          try {
              String sql = String.format("select %s_id from %s where %s_id=?",table,table,table);
              return jdbcTemplate.queryForObject(sql, Integer.class, leagueObject.getId());
        } catch (Throwable ignore) {

        }
        return null;
    }
    
}
