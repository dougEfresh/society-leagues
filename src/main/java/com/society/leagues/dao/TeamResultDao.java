package com.society.leagues.dao;

import com.society.leagues.client.api.TeamResultApi;
import com.society.leagues.client.api.admin.TeamResultAdminApi;
import com.society.leagues.client.api.domain.TeamResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Component
public class TeamResultDao extends Dao<TeamResult> implements TeamResultAdminApi, TeamResultApi {
    @Autowired
    TeamMatchDao teamMatchDao;

    public RowMapper<TeamResult> rowMapper = (rs, rowNum) -> {
        TeamResult result = new TeamResult();
        result.setId(rs.getInt("team_result_id"));
        result.setAwayRacks(rs.getInt("away_racks"));
        result.setHomeRacks(rs.getInt("home_racks"));
        result.setTeamMatch(teamMatchDao.get(rs.getInt("team_match_id")));
        return result;
    };

    @Override
    public String getSql() {
        return "select * from team_result";
    }

    @Override
    public RowMapper<TeamResult> getRowMapper() {
        return rowMapper;
    }

    @Override
    public TeamResult create(TeamResult teamResult) {
        return create(teamResult,getCreateStatement(teamResult,CREATE));
    }

    @Override
    public TeamResult modify(TeamResult teamResult) {
        return modify(teamResult,MODIFY,teamResult.getTeamMatch().getId(),teamResult.getHomeRacks(),teamResult.getAwayRacks(),teamResult.getId());
    }

    public TeamResult getByMatch(Integer id) {
        return get().stream().filter(r -> r.getTeamMatch().getId().equals(id)).findFirst().orElse(null);
    }

    private PreparedStatementCreator getCreateStatement(final TeamResult teamResult, String sql) {
        return con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1,teamResult.getTeamMatch().getId());
            ps.setInt(2,teamResult.getHomeRacks());
            ps.setInt(3,teamResult.getAwayRacks());
            return ps;
        };
    }

    @Override
    public String getIdName() {
        return "team_result_id";
    }

    final static String CREATE = "INSERT INTO team_result(team_match_id,home_racks,away_racks) VALUES (?,?,?)";
    final static String MODIFY = "UPDATE team_result set team_match_id = ?, home_racks = ?, away_racks = ? where team_result_id = ?";
}
