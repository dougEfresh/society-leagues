package com.society.leagues.dao;

import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.SeasonStatus;
import com.society.leagues.client.api.domain.division.Division;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class SeasonDao extends ClientDao<Season> {

    public static RowMapper<Season> rowMapper = (rs, rowNum) -> {
        Division division = DivisionDao.rowMapper.mapRow(rs,rowNum);
        Season season = new Season();
        season.setDivision(division);
        season.setStartDate(rs.getDate("start_date"));
        season.setEndDate(rs.getDate("end_date"));
        season.setName(rs.getString("name"));
        season.setId(rs.getInt("season_id"));
        season.setRounds(rs.getInt("rounds"));
        season.setSeasonStatus(SeasonStatus.valueOf(rs.getString("season_status")));
        return season;
    };

    @Override
    public RowMapper<Season> getRowMapper() {
        return rowMapper;
    }

    @Override
    public Season get(Integer id) {
        return get(id,"select s.*,d.division_type,d.league_type from season s join division d on " +
                "s.division_id=d.division_id where season_id = ?");
    }
}
