package com.society.leagues.dao;

import com.society.leagues.client.api.Role;
import com.society.leagues.client.api.domain.User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthDao extends SocietyDao {

    public User getUser(String username, String password) {
        Map<String, Object> data = jdbcTemplate.queryForMap(
                "SELECT *," +
                        "case when g_name = 'Root' or g_name = 'Operator' then 1 else 0 end as admin" +
                        " From player p left join groups g on p.player_group=g_id " +
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
        user.addRole(Role.USER);

        if ((Integer) data.get("admin") > 0)
            user.addRole(Role.ADMIN);

        return user;
    }
}
