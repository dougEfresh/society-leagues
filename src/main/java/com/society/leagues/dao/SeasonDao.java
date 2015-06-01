package com.society.leagues.dao;

import com.society.leagues.client.api.SeasonClientApi;
import com.society.leagues.client.api.admin.SeasonAdminApi;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Status;
import com.society.leagues.client.api.domain.division.Division;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SeasonDao extends Dao<Season> implements SeasonClientApi,SeasonAdminApi {
    @Autowired DivisionDao divisionDao;

    public  RowMapper<Season> rowMapper = (rs, rowNum) -> {
        Season season = new Season();
        season.setStartDate(rs.getDate("start_date"));
        season.setEndDate(rs.getDate("end_date"));
        season.setName(rs.getString("name"));
        season.setId(rs.getInt("season_id"));
        season.setRounds(rs.getInt("rounds"));
        season.setDivision(divisionDao.get(rs.getInt("division_id")));
        String status = rs.getString("season_status");
        if (status != null && !status.isEmpty()) {
            season.setSeasonStatus(Status.valueOf(status));
        }
        return season;
    };

    public List<Season> getActive() {
        return get().stream().filter(s->s.getSeasonStatus() == Status.ACTIVE).collect(Collectors.toList());
    }
    public List<Season> getInActive() {
        return get().stream().filter(s->s.getSeasonStatus() != Status.ACTIVE).collect(Collectors.toList());
    }

    @Override
    public Season create(Season season) {
        return create(season,getCreateStatement(season,CREATE));
    }

    @Override
    public Boolean delete(Season season) {
        return delete(season,"delete from season where season_id = ?");
    }

    @Override
    public Season modify(Season season) {
        return modify(season,
                "update season set name = ?, start_date = ?, end_date = ? , rounds = ? where season_id = ?"
                ,season.getName()
                ,season.getStartDate()
                ,season.getEndDate()
                ,season.getRounds()
                ,season.getId());
    }

    PreparedStatementCreator getCreateStatement(Season season, String sql) {
        return con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, season.getName());
            ps.setDate(2,new Date(season.getStartDate().getTime()));
            ps.setInt(3,season.getRounds());
            ps.setString(4,season.getSeasonStatus().name());
            return ps;
        };
    }

    final static String CREATE = "INSERT INTO season(name,start_date,rounds,season_status) VALUES (?,?,?,?)";

    @Override
    public String getSql() {
        return "select * from season";
    }

    @Override
    public RowMapper<Season> getRowMapper() {
        return rowMapper;
    }

    @Override
    public String getIdName() {
        return "season_id";
    }

    @Override
    public Season get(String name) {
        return get().stream().filter(s -> s.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
