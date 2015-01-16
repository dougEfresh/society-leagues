package com.society.leagues.dao;

import com.society.leagues.client.api.SeasonClientApi;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.client.api.domain.league.League;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class SeasonDao extends Dao implements SeasonClientApi {

    @Autowired DivisionDao divisionDao;
    
    public static RowMapper<Season> rowMapper = (rs, rowNum) -> {
        Division division = DivisionDao.rowMapper.mapRow(rs,rowNum);
        League league = LeagueDao.rowMapper.mapRow(rs,rowNum);
        division.setLeague(league);
        Season season = new Season();
        season.setDivision(division);
        season.setStartDate(rs.getDate("start_date"));
        season.setEndDate(rs.getDate("end_date"));
        season.setName(rs.getString("name"));
        season.setId(rs.getInt("season_id"));
        season.setRounds(rs.getInt("rounds"));
        return season;
    };
    
    @Override
    public Season get(Integer id) {
        return get("select s.*,d.type,l.league_id,l.league_type" +
                "  from season s " +
                "join division d on s.division_id=d.division_id join " +
                "league l on d.league_id=l.league_id where season_id  = ?",
                id,
                rowMapper);
    }
}
