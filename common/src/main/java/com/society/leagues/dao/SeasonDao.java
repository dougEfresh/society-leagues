package com.society.leagues.dao;

import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Status;
import com.society.leagues.client.api.domain.division.Division;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
public class SeasonDao extends ClientDao<Season> {
    @Autowired Dao dao;
    
    public static RowMapper<Season> rowMapper = (rs, rowNum) -> {
        Season season = new Season();
        season.setStartDate(rs.getDate("start_date"));
        season.setEndDate(rs.getDate("end_date"));
        season.setName(rs.getString("name"));
        season.setId(rs.getInt("season_id"));
        season.setRounds(rs.getInt("rounds"));
        season.setSeasonStatus(Status.valueOf(rs.getString("season_status")));
        return season;
    };

    @Override
    public List<Season> get() {
        return list("select s.* from season s ");
    }

    @Override
    public RowMapper<Season> getRowMapper() {
        return rowMapper;
    }

    @Override
    public Season get(Integer id) {
        return get(id,"select * from season where season_id = ?");
    }
    
    public Season get(String name) {
        return dao.get("select * from season where name = ?", rowMapper,name);
    }
}
