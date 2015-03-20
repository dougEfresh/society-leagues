package com.society.leagues.dao;

import com.society.leagues.client.api.domain.Role;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthDao {
    @Autowired JdbcTemplate jdbcTemplate;

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
