package com.society.leagues.dao;

import com.society.leagues.client.api.UserClientApi;
import com.society.leagues.client.api.admin.UserAdminApi;
import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.Status;
import com.society.leagues.client.api.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserDao extends Dao<User> implements UserClientApi, UserAdminApi{
    @Autowired PlayerDao playerDao;

    public RowMapper<User> rowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setLogin(rs.getString("login"));
        user.setEmail(rs.getString("email"));
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
        return  modify(user,MODIFY,
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
            "email=?," +
            "passwd= ? " +
            " where user_id = ?";

    @Override
    public String getSql() {
        return "select * from users";
    }

    @Override
    public RowMapper<User> getRowMapper() {
        return rowMapper;
    }

    @Override
    public User get(String login) {
        return hydrateUser(super.get().stream().filter(user -> user.getLogin() != null && user.getLogin().equalsIgnoreCase(login)).findFirst().orElse(null));
    }

    public String getPassword(Integer id) {
        return jdbcTemplate.queryForObject("select passwd from users where user_id = ?",String.class,id);
    }

    @Override
    public User get(Integer id) {
        return hydrateUser(copy(super.get(id)));
    }

    public User getWithNoPlayer(Integer id) {
        return super.get().stream().filter(u->u.getId().equals(id)).findFirst().orElse(null);
    }

    public User getWithNoPlayer(String login) {
        return super.get().stream().filter(u -> u.getLogin().equalsIgnoreCase(login)).findFirst().orElseGet(null);
    }

    @Override
    public List<User> get() {
        List<User> users = new ArrayList<>(500);
        users.addAll(super.get().stream().map(u -> hydrateUser(copy(u))).collect(Collectors.toList()));
        return users;
    }

    //TODO Move to constructor?
    private User copy(User u) {
        if (u == null)
            return null;

        User hydratedUser = new User();
        hydratedUser.setId(u.getId());
        hydratedUser.setFirstName(u.getFirstName());
        hydratedUser.setLastName(u.getLastName());
        hydratedUser.setLogin(u.getLogin());
        hydratedUser.setEmail(u.getEmail());
        return hydratedUser;
    }

    private User hydrateUser(User u) {
        try {
            if (u == null)
                return null;

            List<Player> players = playerDao.getByUser(u);
            if (players == null || players.isEmpty())
                return u;


            for (Player p : players) {
                if (p.getSeason().getSeasonStatus() == Status.ACTIVE) {
                    u.addPlayers(Arrays.asList(p));
                    u.addTeams(Arrays.asList(p.getTeam()));
                    u.addSeasons(Arrays.asList(p.getSeason()));
                    u.addDivisions(Arrays.asList(p.getDivision()));
                } else {
                    u.addPastPlayers(Arrays.asList(p));
                    u.addPastTeams(Arrays.asList(p.getTeam()));
                    u.addPastSeasons(Arrays.asList(p.getSeason()));
                    u.addPastDivisions(Arrays.asList(p.getDivision()));
                }
            }
            return u;
        } catch (Throwable t) {
            logger.error(t.getLocalizedMessage(),t);
            return u;
        }
    }
}
