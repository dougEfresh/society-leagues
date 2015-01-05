package com.society.leagues.dao;

import com.society.leagues.client.api.admin.LeagueAdminApi;
import com.society.leagues.client.api.domain.league.League;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Component
public class LeagueAdminDao extends Dao implements LeagueAdminApi {

    private static Logger logger = LoggerFactory.getLogger(LeagueAdminDao.class);

    @Override
    public League create(final League league) {
        try {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(getCreateStatement(league),keyHolder);
            league.setId(keyHolder.getKey().intValue());
        } catch (Throwable t) {
            logger.error(t.getMessage(),t);
            return null;
        }
        return league;
    }

    @Override
    public Boolean delete(final League league) {
        try {
            return jdbcTemplate.update("DELETE from league where league_id = ?",league.getId()) > 0;
        } catch (Throwable t) {
            logger.error(t.getMessage(),t);
        }
        return Boolean.FALSE;
    }

    @Override
    public League modify(League league) {
        try {
            if (jdbcTemplate.update("UPDATE league SET league_type = ? WHERE league_id  = ?", league.getType().name() , league.getId()) <= 0)
                return null;

            return league;
        } catch (Throwable t) {
            logger.error(t.getMessage(),t);
        }
        return null;
    }


    private PreparedStatementCreator getCreateStatement(final League league) {
        return con -> {
            PreparedStatement ps = con.prepareStatement(CREATE, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, league.getType().name());
            return ps;
        };
    }

    final static String CREATE = "INSERT INTO league(league_type) VALUES (?)";
}
