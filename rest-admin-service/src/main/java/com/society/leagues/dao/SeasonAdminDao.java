package com.society.leagues.dao;

import com.society.leagues.client.api.admin.SeasonAdminApi;
import com.society.leagues.client.api.domain.Season;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class SeasonAdminDao extends Dao implements SeasonAdminApi {
    private static Logger logger = LoggerFactory.getLogger(SeasonAdminDao.class);

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

    final static String CREATE = "INSERT INTO season(name,start_date,rounds,season_status) VALUES (?,?,?,?)";}
