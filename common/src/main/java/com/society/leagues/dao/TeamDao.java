package com.society.leagues.dao;

import com.society.leagues.client.api.TeamClientApi;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.division.Division;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
public class TeamDao extends ClientDao<Team> implements TeamClientApi {
    
    public static RowMapper<Team> rowMapper = (rs, rowNum) -> {
        Team team = new Team(rs.getString("name"));
        team.setCreated(rs.getDate("created"));
        team.setId(rs.getInt("team_id"));
        return team;
    };

    @Override
    public List<Team> get() {
        return list("select t.*,d.league_type from team t join division d on t.division_id=d.division_id");
    }

    @Override
    public Team get(String name) {
        List<Team>  teams = list("select * from team where name = ?",name);
        if (teams == null || teams.isEmpty()) 
            return null;
        
        return teams.get(0);
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
