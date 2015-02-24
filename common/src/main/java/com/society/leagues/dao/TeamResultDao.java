package com.society.leagues.dao;

import com.society.leagues.client.api.TeamResultApi;
import com.society.leagues.client.api.admin.TeamResultAdminApi;
import com.society.leagues.client.api.domain.Match;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.TeamResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TeamResultDao extends Dao<TeamResult> implements TeamResultAdminApi, TeamResultApi {
    @Autowired MatchDao matchDao;

    public RowMapper<TeamResult> rowMapper = (rs, rowNum) -> {
        TeamResult result = new TeamResult();
        Match match = matchDao.get(rs.getInt("team_match_id"));
        result.setId(rs.getInt("team_result_id"));
        result.setAwayRacks(rs.getInt("away_racks"));
        result.setHomeRacks(rs.getInt("home_racks"));
        result.setMatch(match);
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
        return modify(teamResult,MODIFY,teamResult.getMatch().getId(),teamResult.getHomeRacks(),teamResult.getAwayRacks(),teamResult.getId());
    }

    @Override
    public List<TeamResult> getByTeam(Team team) {
        return get().stream().filter( t -> t.getMatch().getHome().equals(team) || t.getMatch().getAway().equals(team)).collect(Collectors.toList());
    }

    private PreparedStatementCreator getCreateStatement(final TeamResult teamResult, String sql) {
        return con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1,teamResult.getMatch().getId());
            ps.setInt(2,teamResult.getHomeRacks());
            ps.setInt(3,teamResult.getAwayRacks());
            return ps;
        };
    }

    final static String CREATE = "INSERT INTO team_result(team_match_id,home_racks,away_racks) VALUES (?,?,?)";
    final static String MODIFY = "UPDATE team_result set team_match_id = ?, home_racks = ?, away_racks = ? where team_result_id = ?";
}
