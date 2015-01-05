package com.society.leagues.dao;

import com.society.leagues.client.api.domain.LeagueObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.util.List;
import java.util.Map;

public abstract class Dao {
    private static Logger logger = LoggerFactory.getLogger(Dao.class);
    @Autowired public JdbcTemplate jdbcTemplate;

    public Map<String,Object> queryForMap(String query, Object... obj) {
        return jdbcTemplate.queryForMap(query,obj);
    }

    public List<Map<String,Object>> queryForListMap(String query, Object... obj) {
        return jdbcTemplate.queryForList(query,obj);
    }

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
}
