package com.society.leagues.dao;

import com.society.leagues.client.admin.api.TeamAdminApi;
import com.society.leagues.client.api.domain.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;

@Component
public class TeamAdminDao extends Dao implements TeamAdminApi {

    private static Logger logger = LoggerFactory.getLogger(TeamAdminDao.class);

    @Override
    public Team create(Team team) {
        final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(getCreateStatement(team),keyHolder);
            team.setId(keyHolder.getKey().intValue());
            return team;
        } catch (Throwable t) {
            logger.error(t.getMessage(),t);
        }
        return null;
    }

    @Override
    public Team modify(Team team) {
        try {
            if (jdbcTemplate.update("UPDATE team SET captain_id = ? , league_id = ? WHERE team_id = ?",
                    team.getCaptain(), team.getLeague().getId(), team.getId()) <= 0)
                return null;

            return team;
        } catch (Throwable t){
            logger.error(t.getMessage(),t);
        }
        return null;
    }

     private PreparedStatementCreator getCreateStatement(final Team team) {
        return con -> {
            PreparedStatement ps = con.prepareStatement(CREATE, Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            ps.setInt(i++, team.getLeague().getId());
            ps.setString(i++, team.getName());
            if (team.getCaptain() == null)
                ps.setNull(i++, Types.INTEGER);
            else
                ps.setInt(i++, team.getCaptain());
            return ps;
        };
    }
    final static String CREATE = "INSERT INTO team (league_id,name,captain_id) VALUES (?,?,?)";
}
