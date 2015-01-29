package com.society.leagues.dao;

import com.society.leagues.client.api.UserClientApi;
import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Primary
public class UserDao extends ClientDao<User> implements UserClientApi {
    
    public static RowMapper<User> rowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        return user;
    };
    
    static Logger logger = LoggerFactory.getLogger(UserDao.class);

    @Autowired PlayerDao playerDao;

    @Override
    public List<User> current(List<User> users) {
        return processUsers(users,playerDao.current(users));
    }

    @Override
    public List<User> all(List<User> users) {
        return processUsers(users, playerDao.all(users));
    }

    @Override
    public List<User> past(List<User> users) {
        return processUsers(users,playerDao.past(users));
    }

    private List<User> processUsers(List<User> users, List<Player> players) {
        try {
            Map<Integer, User> map = new HashMap<>();

            for (User user : users) {
                map.put(user.getId(), new User(user.getId()));
            }

            if (map.isEmpty())
                return Collections.emptyList();

            for (Player p : players) {
                User u = map.get(p.getUser().getId());
                u.addPlayers(Arrays.asList(p));
                u.addTeams(Arrays.asList(p.getTeam()));
                u.addSeasons(Arrays.asList(p.getSeason()));
                u.addDivisions(Arrays.asList(p.getDivision()));
            }

            return  Arrays.asList(map.values().toArray(new User[]{}));
        } catch (Throwable t) {
            logger.error(t.getLocalizedMessage(),t);
            return null;
        }
    }

    @Override
    public RowMapper<User> getRowMapper() {
        return rowMapper;
    }

    @Override
    public List<User> get() {
        return list("select * from users");
    }

    @Override
    public User get(String login) {
        List<User> users = list("select * from users where login = ?",login);
        if (users == null || users.isEmpty())
            return null;
        
        return users.get(0);
    }
    
    
}
