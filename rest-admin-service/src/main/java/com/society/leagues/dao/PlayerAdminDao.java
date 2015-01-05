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
        return create(player,getCreateStatement(player,CREATE));
    }

    @Override
    public Boolean delete(final Player player) {
        return delete(player,"UPDATE player set password = null WHERE player_id = ?");
    }

    @Override
    public Player modify(final Player player) {
        return modify(player,MODIFY,
                    player.getLogin(),
                    player.getRole().id,
                    player.getEmail(),
                    player.getFirstName(),
                    player.getLastName(),
                    player.getEmail(),
                    player.getPassword(),
                    player.getId()
            );

    }

    protected PreparedStatementCreator getCreateStatement(final LeagueObject leagueObject, String sql) {
        final Player player = (Player) leagueObject;
        return con -> {
            PreparedStatement ps = con.prepareStatement(CREATE,Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            ps.setString(i++, player.getLogin());
            ps.setInt(i++, player.getRole().id);
            ps.setString(i++, player.getFirstName());
            ps.setString(i++, player.getLastName());
            ps.setString(i++, player.getEmail());
            ps.setString(i, player.getPassword());

            return ps;
        };
    }

    static String CREATE = "INSERT INTO player " +
            "(" +
            "player_login," +
            "player_group," +
            "first_name," +
            "last_name," +
            "email," +
            "password) " +
            "VALUES " +
            "(?,?,?,?,?,?)";

    static String MODIFY = "UPDATE player " +
            "set " +
            "player_login=?," +
            "player_group=?," +
            "first_name=?," +
            "last_name=?," +
            "email=?," +
            "`password`= ? " +
            " where player_id = ?";
}
