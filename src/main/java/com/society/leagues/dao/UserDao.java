package com.society.leagues.dao;

import com.society.leagues.client.api.UserClientApi;
import com.society.leagues.client.api.admin.UserAdminApi;
import com.society.leagues.client.api.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Component
public class UserDao extends Dao<User> implements UserClientApi, UserAdminApi{
    public RowMapper<User> rowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setLogin(rs.getString("login"));
        user.setEmail(rs.getString("email"));
        user.setRole(Role.valueOf(rs.getString("role")));
        user.setStatus(Status.valueOf(rs.getString("status")));
        return user;
    };

    static Logger logger = LoggerFactory.getLogger(UserDao.class);

    @Override
    public User create(User user) {
        logger.debug("Creating user: " + user.getLogin());
        User u = create(user,getCreateStatement(user));
        if (u == null)
            return null;

        u.setPassword(null);
        return u;
    }

    @Override
    public Boolean delete(User user) {
        return  delete(user,"UPDATE users set status = 0 where user_id = ?");
    }

    @Override
    public User modify(User user) {
        return  modify(user, MODIFY,
                user.getLogin(),
                user.getRole().name(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getId()
        );
    }

    protected PreparedStatementCreator getCreateStatement(final User user) {
        return con -> {
            PreparedStatement ps = con.prepareStatement(CREATE, Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            ps.setString(i++, user.getLogin());
            ps.setString(i++, user.getRole().name());
            ps.setString(i++, user.getFirstName());
            ps.setString(i++, user.getLastName());
            ps.setString(i++, user.getEmail());
            ps.setString(i, new BCryptPasswordEncoder().encode(user.getPassword()));
            return ps;
        };
    }
    static String CREATE = "INSERT INTO users " +
            "(" +
            "login," +
            "role," +
            "first_name," +
            "last_name," +
            "email," +
            "passwd) " +
            "VALUES " +
            "(?,?,?,?,?,?)";

    static String MODIFY = "UPDATE users " +
            "set " +
            "login=?," +
            "role=?," +
            "first_name=?," +
            "last_name=?," +
            "email=?" +
            " where user_id = ?";

    @Override
    public String getSql() {
        return "select * from users";
    }

    @Override
    public RowMapper<User> getRowMapper() {
        return rowMapper;
    }

    public String getPassword(Integer id) {
        return jdbcTemplate.queryForObject("select passwd from users where user_id = ?",String.class,id);
    }

    @Override
    public User get(String login) {
        return get().stream().filter(u -> u.getLogin().equalsIgnoreCase(login)).findFirst().orElse(null);
    }

    @Override
    public String getIdName() {
        return "user_id";
    }
}
