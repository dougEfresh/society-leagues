package com.society.leagues.dao;

import com.society.leagues.client.api.TeamClientApi;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.division.Division;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
public class TeamDao extends ClientDao<Team> implements TeamClientApi {
    @Autowired Dao dao;
    
    public static RowMapper<Team> rowMapper = (rs, rowNum) -> {
        Team team = new Team(rs.getString("name"));
        team.setCreated(rs.getDate("created"));
        team.setId(rs.getInt("team_id"));
        return team;
    };

    @Override
    public List<Team> get() {
        return list("select * from team");
    }

    @Override
    public Team get(String name) {
        return dao.get("select * from team where name = ?",rowMapper,name);
    }

    @Override
    public Team get(Integer id) {
        return get(id,"select * from team  where team_id = ?");
    }

    @Override
    public RowMapper<Team> getRowMapper() {
        return rowMapper;
    }
    
}
