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

    RowMapper<PlayerResult> rowMapper = (rs, rowNum) -> {
        PlayerResult result = new PlayerResult();
        result.setAwayRacks(rs.getInt("away_racks"));
        result.setHomeRacks(rs.getInt("home_racks"));
        result.setPlayerAwayId(rs.getInt("player_away_id"));
        result.setPlayerHomeId(rs.getInt("player_home_id"));
        result.setTeamMatchId(rs.getInt("team_matc_id"));
        return result;
    };

     protected PreparedStatementCreator getCreateStatement(final PlayerResult result, String sql) {
        return con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int j=0;
            ps.setInt(++j,result.getTeamMatchId());
            ps.setInt(++j,result.getPlayerHomeId());
            ps.setInt(++j,result.getPlayerAwayId());
            ps.setInt(++j,result.getHomeRacks());
            ps.setInt(++j,result.getAwayRacks());
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
                playerResult.getTeamMatchId(),
                playerResult.getPlayerHomeId(),
                playerResult.getPlayerAwayId(),
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

    static String CREATE = "insert into player_result VALUES(?,?,?,?)";
}
