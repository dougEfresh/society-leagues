package com.society.leagues.dao;

import com.society.leagues.client.admin.api.PlayerAdminApi;
import com.society.leagues.client.api.Role;
import com.society.leagues.client.api.domain.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PlayerDao extends SocietyDao implements PlayerAdminApi {
    private static Logger logger = LoggerFactory.getLogger(PlayerDao.class);

    @Override
    public Player create(Player player) {
        Role role = player.getRole();

        try {
            jdbcTemplate.update(CREATE,
                    player.getLogin(),
                    role.id,
                    player.getFirstName(),
                    player.getLastName(),
                    player.getEmail(),
                    player.getPassword()
            );
            Integer id = jdbcTemplate.queryForObject(
                    "select player_id from player where player_login = ?",
                    Integer.class,
                    player.getLogin());

            player.setId(id);
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
