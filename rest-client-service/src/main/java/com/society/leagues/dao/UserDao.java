package com.society.leagues.dao;

import com.society.leagues.client.api.Role;
import com.society.leagues.client.api.UserApi;
import com.society.leagues.client.api.domain.User;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDao extends ClientDao<User> implements UserApi {
    public static RowMapper<User> rowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        return user;
    };

    @Override
    public List<User> current(List<User> users) {
        return super.current(users);
    }

    @Override
    public List<User> current(Integer userId) {
        return
    }

    @Override
    public RowMapper<User> getRowMapper() {
        return null;
    }

    @Override
    public User get(Integer id) {
        return get(id,"select * from users where user_id = ?");
    }

    final static String CURRENT_TEAMS = "select t.* from player p " +
            " JOIN team t   on p.team_id=t.team_id" +
            " JOIN season s on p.season_id=s.season_id and " +
            " JOIN division d on p.division_id=d.division_id " +
            " JOIN league l on d.league_id";
    
}
