package com.society.leagues.dao;

import com.society.leagues.client.api.admin.TeamAdminApi;
import com.society.leagues.client.api.domain.LeagueObject;
import com.society.leagues.client.api.domain.Team;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Component
public class TeamAdminDao extends Dao implements TeamAdminApi {

    @Override
    public Team create(Team team) {
        return create(team,getCreateStatement(team,CREATE));
    }

    @Override
    public Boolean delete(Team team) {
        return delete(team,"delete from team WHERE team_id = ?");
    }

    protected PreparedStatementCreator getCreateStatement(final LeagueObject leagueObject, String sql) {
         Team team = (Team) leagueObject;
        return con -> {
            PreparedStatement ps = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, team.getName());
            return ps;
        };
    }

    final static String CREATE = "INSERT INTO team (name) VALUES (?)";
}
