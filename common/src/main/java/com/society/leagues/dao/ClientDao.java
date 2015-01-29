package com.society.leagues.dao;

import com.society.leagues.client.api.ClientApi;
import com.society.leagues.client.api.domain.LeagueObject;
import com.society.leagues.client.api.domain.Status;
import com.society.leagues.client.api.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.*;

public abstract class ClientDao<Q extends LeagueObject> implements ClientApi<Q> {
    //TODO Some Major Cache
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired NamedParameterJdbcTemplate namedJdbcTemplate;
    private static Logger logger = LoggerFactory.getLogger(ClientDao.class);
    
    public static final String CLIENT_REQUEST = "" +
            "SELECT p.*," +
            "t.name,t.created," +
            "d.division_type,d.league_type," +
            "s.*," +
            "u.first_name,u.last_name" +
             " from player p join team t on t.team_id=p.team_id " +
             " join season s on s.season_id=p.season_id " +
             " join division d on d.division_id=p.division_id " +
             " join users u on u.user_id=p.user_id ";

    public List<Q> get(List<User> users, Status status) {
        try {
            Map<String, Object> params = new HashMap<>();
            List<Integer> ids = new ArrayList<>();
            users.stream().forEach(u -> ids.add(u.getId()));
            params.put("userids", ids);
            if (status != null) {
                params.put("seasonStatus", status.name());
                return new ArrayList<Q>(
                        new LinkedHashSet<Q>(namedJdbcTemplate.query(
                                CLIENT_REQUEST +
                                        "where season_status = :seasonStatus and u.user_id in (:userids)",
                                params,
                            getRowMapper())
                        )
                        
                );
            }
            return new ArrayList<Q>(
                        new LinkedHashSet<Q>(namedJdbcTemplate.query(
                                CLIENT_REQUEST +
                                        "where u.user_id in (:userids)",
                                params,
                                getRowMapper())
                        ));
        } catch (Throwable t){
            logger.error(t.getLocalizedMessage(),t);
        }
        return null;
    }

    @Override
    public List<Q> current(List<User> users) {
            return get(users, Status.ACTIVE);
    }
    
    @Override
    public List<Q> past(List<User> users) {
            return get(users, Status.INACTIVE);
    }
    
    @Override
    public List<Q> all(List<User> users) {
        return get(users, null);
    }

    @Override
    public List<Q> current(Integer userId) {
        return current(Arrays.asList(new User(userId)));
    }
    
     
    @Override
    public List<Q> past(Integer userId) {
        return past(Arrays.asList(new User(userId)));
    }

    @Override
    public List<Q> all(Integer userId) {
        return all(Arrays.asList(new User(userId)));
    }

    public Q get(Integer userId) {
        List<Q> list = current(Arrays.asList(new User(userId)));
        if (list == null || list.isEmpty())
            return null;

        return list.get(0);

    }

    public Q get(Integer id, String sql) {
         try {
             List<Q> list = jdbcTemplate.query(sql, getRowMapper(), id);
             if (list.isEmpty())
                 return null;
             return list.get(0);
         } catch (Throwable t){
             logger.error(t.getLocalizedMessage(),t);
         }
         return null;
    }

    public List<Q> list(String sql,Object ...args) {
        try {
            return new ArrayList<>(
                    new LinkedHashSet<>(jdbcTemplate.query(sql, getRowMapper(),args))
            );
        } catch (Throwable t){
            logger.error(t.getLocalizedMessage(),t);
        }
        return null;
    }

    public abstract RowMapper<Q> getRowMapper();
}
