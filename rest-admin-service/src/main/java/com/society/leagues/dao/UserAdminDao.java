package com.society.leagues.dao;

import com.society.leagues.client.api.admin.UserAdminApi;
import com.society.leagues.client.api.domain.User;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Component
public class UserAdminDao extends Dao implements UserAdminApi {

    @Override
    public User create(User user) {
        User u = create(user,getCreateStatement(user));
        u.setPassword(null);
        return u;
    }

    @Override
    public Boolean delete(User user) {
        return delete(user,"UPDATE challengeUsers set status = 0 where user_id = ?");
    }

    @Override
    public User modify(User user) {
        return modify(user,MODIFY,
                user.getLogin(),
                user.getRole().name(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
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
            ps.setString(i, user.getPassword());

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

    static String MODIFY = "UPDATE challengeUsers " +
            "set " +
            "login=?," +
            "role=?," +
            "first_name=?," +
            "last_name=?," +
            "email=?," +
            "passwd= ? " +
            " where user_id = ?";
}
