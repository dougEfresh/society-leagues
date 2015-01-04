package com.society.leagues.dao;

import com.society.leagues.client.api.Role;
import com.society.leagues.client.api.domain.User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthDao extends Dao {

    public User getUser(String username, String password) {
        Map<String, Object> data = jdbcTemplate.queryForMap(
                "SELECT *" +
                        " From player p " +
                        " WHERE p.player_login = ? " +
                        " AND p.`password` = ?",
                username,
                password
        );

        User user = new User();
        user.setFirstName((String) data.get("first_name"));
        user.setLastName((String) data.get("last_name"));
        user.setLogin((String) data.get("player_login"));
        user.setId((Integer) data.get("player_id"));
        user.setRole(Role.fromId((Integer) data.get("player_group")));

        return user;
    }
}
