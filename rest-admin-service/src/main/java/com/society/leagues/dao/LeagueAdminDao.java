package com.society.leagues.dao;

import com.society.leagues.client.api.admin.LeagueAdminApi;
import com.society.leagues.client.api.domain.LeagueObject;
import com.society.leagues.client.api.domain.league.League;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Component
public class LeagueAdminDao extends Dao implements LeagueAdminApi {

    @Override
    public League create(final League league) {
        return create(league,getCreateStatement(league,CREATE));
    }

    @Override
    public Boolean delete(final League league) {
        return delete(league,"DELETE from league where league_id = ?");
    }

    @Override
    public League modify(League league) {
        return modify(league, "UPDATE league SET league_type = ? WHERE league_id  = ?", league.getType().name() , league.getId());
    }

    protected PreparedStatementCreator getCreateStatement(LeagueObject leagueObject, String sql) {
        League league = (League) leagueObject;
        return con -> {
            PreparedStatement ps = con.prepareStatement(CREATE, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, league.getType().name());
            return ps;
        };
    }

    final static String CREATE = "INSERT INTO league(league_type) VALUES (?)";
}
