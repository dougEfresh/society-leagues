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
                        " From users  " +
                        " WHERE login = ? " +
                        " AND passwd = ?",
                username,
                password
        );

        User user = new User();
        user.setFirstName((String) data.get("first_name"));
        user.setLastName((String) data.get("last_name"));
        user.setLogin((String) data.get("login"));
        user.setId((Integer) data.get("user_id"));
        user.setRole(Role.valueOf((String) data.get("role")));

        return user;
    }
}
