package com.society.leagues.dao;

import com.society.leagues.client.api.Role;
import com.society.leagues.client.api.UserApi;
import com.society.leagues.client.api.domain.User;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class UserDao implements UserApi {
    public static RowMapper<User> rowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setFirstName(rs.getString("first_name"));
        user.setFirstName(rs.getString("last_name"));
        user.setRole(Role.valueOf(rs.getString("role")));
        return user;
    };
    
    @Override
    public User info(Integer id) {
        //PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory()
        return null;
    }
    
    final static String CURRENT_TEAMS = "select t.* from player p " +
            " JOIN team t   on p.team_id=t.team_id" +
            " JOIN season s on p.season_id=s.season_id and " +
            " JOIN division d on p.division_id=d.division_id " +
            " JOIN league l on d.league_id";
    
}
