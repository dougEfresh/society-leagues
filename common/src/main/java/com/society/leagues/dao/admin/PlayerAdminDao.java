package com.society.leagues.dao.admin;

import com.society.leagues.client.api.admin.PlayerAdminApi;
import com.society.leagues.client.api.domain.Player;

import com.society.leagues.dao.Dao;
import com.society.leagues.dao.PlayerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Component
public class PlayerAdminDao extends PlayerDao implements PlayerAdminApi {
    @Autowired Dao dao;
    
    @Override
    public Player create(final Player player) {
        return dao.create(player,getCreateStatement(player));
    }

    @Override
    public Boolean delete(final Player player) {
        return dao.delete(player,"delete from player WHERE player_id = ?");
    }

    @Override
    public Player modify(final Player player) {
        return dao.modify(player,MODIFY,
                player.getSeason().getId(),
                player.getUser().getId(),
                player.getTeam().getId(),
                player.getStart(),player.getEnd(),
                player.getId()
            );

    }

    protected PreparedStatementCreator getCreateStatement(final Player player) {
        return con -> {
            PreparedStatement ps = con.prepareStatement(CREATE,Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            ps.setInt(i++, player.getSeason().getId());
            ps.setInt(i++, player.getDivision().getId());
            ps.setInt(i++, player.getUser().getId());
            ps.setInt(i++, player.getTeam().getId());
            ps.setInt(i++, player.getHandicap().ordinal());
            
            if (player.getStart() != null)
                ps.setDate(i++, new java.sql.Date(player.getStart().getTime()));
            else 
                ps.setDate(i++, null);
            
            if (player.getEnd() != null)
                ps.setDate(i++, new java.sql.Date(player.getEnd().getTime()));
            else
                ps.setDate(i++, null);

            return ps;
        };
    }

    static String CREATE = "INSERT INTO player " +
            "(" +
            "season_id," +
            "division_id," +
            "user_id," +
            "team_id," +
            "handicap," +
            "start_date," +
            "end_date) " +
            "VALUES " +
            "(?,?,?,?,?,?,?)";

    static String MODIFY = "UPDATE player " +
            "set " +
            "season_id=?," +
            "user_id=?," +
            "team_id=?," +
            "handicap=?," +
            "start_date=?," +
            "end_date=?," +
            " where player_id = ?";
}
