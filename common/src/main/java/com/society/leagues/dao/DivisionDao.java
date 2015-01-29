package com.society.leagues.dao;

import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.client.api.domain.division.DivisionType;
import com.society.leagues.client.api.domain.division.LeagueType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

@Component @Primary
public class DivisionDao extends ClientDao<Division> {
    @Autowired Dao dao;

    public static RowMapper<Division> rowMapper = (rs, rowNum) -> {
        Division division = new Division();
        division.setId(rs.getInt("division_id"));
        division.setType(DivisionType.valueOf(rs.getString("division_type")));
        division.setLeague(LeagueType.valueOf(rs.getString("league_type")));
        return division;
    };

    @Override
    public Division get(Integer id) {
        return get(id,"select * from division where division_id = ?");
    }

    @Override
    public List<Division> get() {
        return list("select * from division");
    }

    @Override
    public RowMapper<Division> getRowMapper() {
        return rowMapper;
    }
}
