package com.society.leagues.dao;

import com.society.leagues.client.admin.api.PlayerAdminApi;
import com.society.leagues.client.api.Role;
import com.society.leagues.client.api.domain.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Component
public class PlayerAdminDao extends Dao implements PlayerAdminApi {
    private static Logger logger = LoggerFactory.getLogger(PlayerAdminDao.class);

    @Override
    public Player create(Player player) {
        final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(getCreateStatement(player), keyHolder);
            player.setId(keyHolder.getKey().intValue());
            return player;
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
        return null;
    }

    @Override
    public Boolean delete(Integer id) {
        try {
            return jdbcTemplate.update("UPDATE player set password = null WHERE player_id = ?", id) > 0;
        } catch (Throwable t){
            logger.error(t.getMessage(),t);
        }
        return Boolean.FALSE;
    }

    @Override
    public Player modify(Player player) {
           if (!player.verify() || player.getId() < 1) {
            logger.error("Could not verify player: " + player);
            return null;
        }
        Role role = player.getRole();

        try {
            if (jdbcTemplate.update(MODIFY,
                    player.getLogin(),
                    role.id,
                    player.getEmail(),
                    player.getFirstName(),
                    player.getLastName(),
                    player.getEmail(),
                    player.getPassword(),
                    player.getId()
            ) != 1) {
                logger.error("Could not update player: " + player);
                return null;
            }
            player.setPassword(null);
            return player;
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
        return null;
    }

    private PreparedStatementCreator getCreateStatement(final Player player) {
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
