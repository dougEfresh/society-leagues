package com.society.leagues.dao;

import com.society.leagues.client.api.domain.PlayerResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Component
public class PlayerResultDao extends Dao<PlayerResult> {
    @Autowired PlayerDao playerDao;
    @Autowired TeamMatchDao teamMatchDao;

    RowMapper<PlayerResult> rowMapper = (rs, rowNum) -> {
        PlayerResult result = new PlayerResult();
        result.setAwayRacks(rs.getInt("away_racks"));
        result.setHomeRacks(rs.getInt("home_racks"));
        result.setPlayerAway(playerDao.get(rs.getInt("player_away_id")));
        result.setPlayerHome(playerDao.get(rs.getInt("player_home_id")));
        result.setTeamMatch(teamMatchDao.get(rs.getInt("team_match_id")));
        return result;
    };

     protected PreparedStatementCreator getCreateStatement(final PlayerResult result, String sql) {
        return con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int j=0;
            ps.setInt(1,result.getTeamMatch().getId());
            ps.setInt(2,result.getPlayerHome().getId());
            ps.setInt(3,result.getPlayerAway().getId());
            ps.setInt(4,result.getHomeRacks());
            ps.setInt(5,result.getAwayRacks());
            return ps;
        };
    }


    public PlayerResult create(PlayerResult thing) {
        return super.create(thing, getCreateStatement(thing,CREATE));
    }

    public PlayerResult modify(PlayerResult playerResult) {
        return modify(playerResult,
                "update player_result set " +
                        "team_match_id =?, " +
                        "home_player_id=?," +
                        "away_player_id=?," +
                        "home_racks=?," +
                        "away_racks=? " +
                        "where player_result_id = ?",
                playerResult.getTeamMatch().getId(),
                playerResult.getPlayerHome().getId(),
                playerResult.getPlayerAway().getId(),
                playerResult.getHomeRacks(),
                playerResult.getAwayRacks(),
                playerResult.getId());
    }

    @Override
    public String getSql() {
        return "select * from player_result";
    }

    @Override
    public RowMapper<PlayerResult> getRowMapper() {
        return rowMapper;
    }

    static String CREATE = "insert into player_result (team_match_id,player_home_id,player_away_id,home_racks,away_racks) VALUES(?,?,?,?,?)";
}
