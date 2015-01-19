package com.society.leagues.dao;

import com.society.leagues.client.api.admin.DivisionAdminApi;
import com.society.leagues.client.api.domain.division.Division;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;


@Component
public class DivisionAdminDao extends Dao implements DivisionAdminApi {
    private static Logger logger = LoggerFactory.getLogger(DivisionAdminDao.class);

    @Override
    public Division create(Division division) {
        return create(division,getCreateStatement(division,CREATE));
    }

    @Override
    public Boolean delete(Division division) {
        return delete(division,"DELETE from division where division_id = ?");
    }

    @Override
    public Division modify(Division division) {
        return modify(division,"UPDATE division SET division_type = ?, league_type = ? WHERE division_id  = ?",
                division.getType().name(),division.getLeague().name(),division.getId());
    }

    protected PreparedStatementCreator getCreateStatement(final Division division, String sql) {
        return con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, division.getLeague().name());
            ps.setString(2, division.getType().name());
            return ps;
        };
    }

    final static String CREATE = "INSERT INTO division(league_type,division_type) VALUES (?,?)";
}

