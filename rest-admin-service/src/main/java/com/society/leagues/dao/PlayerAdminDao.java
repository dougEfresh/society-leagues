package com.society.leagues.dao;

import com.society.leagues.client.api.admin.PlayerAdminApi;
import com.society.leagues.client.api.domain.LeagueObject;
import com.society.leagues.client.api.domain.Player;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Component
public class PlayerAdminDao extends Dao implements PlayerAdminApi {
    @Override
    public Player create(final Player player) {
        return create(player,getCreateStatement(player));
    }

    @Override
    public Boolean delete(final Player player) {
        return delete(player,"delete from player WHERE player_id = ?");
    }

    @Override
    public Player modify(final Player player) {
        return modify(player,MODIFY,
                player.getSeason().getId(),
                player.getUser().getId(),
                player.getTeam().getId(),
                player.getId()
            );

    }

    protected PreparedStatementCreator getCreateStatement(final Player player) {
        return con -> {
            PreparedStatement ps = con.prepareStatement(CREATE,Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            ps.setInt(i++, player.getSeason().getId());
            ps.setInt(i++, player.getUser().getId());
            ps.setInt(i++, player.getTeam().getId());
            ps.setString(i++, player.getHandicap());
            return ps;
        };
    }

    static String CREATE = "INSERT INTO player " +
            "(" +
            "season_id,user_id,team_id,handicap) " +
            "VALUES " +
            "(?,?,?,?)";

    static String MODIFY = "UPDATE player " +
            "set " +
            "season_id=?," +
            "user_id=?," +
            "team_id=?," +
            "handicap=?," +
            " where player_id = ?";
}
