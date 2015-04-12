package com.society.leagues.dao;

import com.society.leagues.client.api.TeamClientApi;
import com.society.leagues.client.api.admin.TeamAdminApi;
import com.society.leagues.client.api.domain.Team;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Component
public class TeamDao extends Dao<Team> implements TeamClientApi, TeamAdminApi {

    public static RowMapper<Team> rowMapper = (rs, rowNum) -> {
        Team team = new Team(rs.getString("name"));
        team.setCreated(rs.getDate("created"));
        team.setId(rs.getInt("team_id"));
        return team;
    };

    @Override
    public Team create(Team team) {
        return create(team,getCreateStatement(team,CREATE));
    }

    @Override
    public Boolean delete(Team team) {
        return delete(team, "delete from team WHERE team_id = ?");
    }

    protected PreparedStatementCreator getCreateStatement(final Team team, String sql) {
        return con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, team.getName());
            return ps;
        };
    }

    @Override
    public Team get(String name) {
        return get().stream().filter(f -> f.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public String getSql() {
        return "select * from team";
    }

    @Override
    public RowMapper<Team> getRowMapper() {
        return rowMapper;
    }

    final static String CREATE = "INSERT INTO team (name) VALUES (?)";

}
