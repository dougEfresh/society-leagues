package com.society.leagues.dao;

import com.society.leagues.client.api.LeagueClientApi;
import com.society.leagues.client.api.domain.league.League;
import com.society.leagues.client.api.domain.league.LeagueType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class LeagueDao extends Dao implements LeagueClientApi {

    public static RowMapper<League> rowMapper = (rs, rowNum) -> {
        League league = new League();
        league.setType(LeagueType.valueOf(rs.getString("league_type")));
        league.setId(rs.getInt("league_id"));
        return league;
    };
    
    @Override
    public List<League> list() {
        return Collections.emptyList();
    }

    @Override
    public League get(Integer id) {
        return get("select * from league where league_id = ?", new Object[] {id}, rowMapper);
    }
}
