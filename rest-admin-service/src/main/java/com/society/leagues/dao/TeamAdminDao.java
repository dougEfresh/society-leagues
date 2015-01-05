package com.society.leagues.dao;

import com.society.leagues.client.api.admin.TeamAdminApi;
import com.society.leagues.client.api.domain.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Date;

@Component
public class TeamAdminDao extends Dao implements TeamAdminApi {

    private static Logger logger = LoggerFactory.getLogger(TeamAdminDao.class);

    @Override
    public Team create(Team team) {
        final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(getCreateStatement(team),keyHolder);
            team.setId(keyHolder.getKey().intValue());
            team.setCreated(new Date());
            return team;
        } catch (Throwable t) {
            logger.error(t.getMessage(),t);
        }
        return null;
    }

    @Override
    public Boolean delete(Team team) {
        try {
            return  (jdbcTemplate.update("delete from team WHERE team_id = ?",team.getId() ) > 0);
        } catch (Throwable t){
            logger.error(t.getMessage(),t);
        }
        return Boolean.FALSE;
    }

     private PreparedStatementCreator getCreateStatement(final Team team) {
        return con -> {
            PreparedStatement ps = con.prepareStatement(CREATE, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, team.getName());
            return ps;
        };
    }

    final static String CREATE = "INSERT INTO team (name) VALUES (?)";
}
