package com.society.leagues.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

public class SocietyDao {
    @Autowired
    public JdbcTemplate jdbcTemplate;

    public Map<String,Object> queryForMap(String query, Object... obj) {
        return jdbcTemplate.queryForMap(query,obj);
    }

    public List<Map<String,Object>> queryForListMap(String query, Object... obj) {
        return jdbcTemplate.queryForList(query,obj);
    }
}
