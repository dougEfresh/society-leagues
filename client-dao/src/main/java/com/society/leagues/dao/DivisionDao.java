package com.society.leagues.dao;

import com.society.leagues.client.api.DivisionClientApi;
import com.society.leagues.client.api.admin.DivisionAdminApi;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.client.api.domain.division.DivisionType;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.RowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Component 
public class DivisionDao extends Dao<Division> implements DivisionAdminApi, DivisionClientApi {

    public static RowMapper<Division> rowMapper = (rs, rowNum) -> {
        Division division = new Division();
        division.setId(rs.getInt("division_id"));
        division.setType(DivisionType.valueOf(rs.getString("division_type")));
        return division;
    };

    @Override
    public Division create(Division division) {
        return create(division, getCreateStatement(division,CREATE));
    }

    @Override
    public Boolean delete(Division division) {
        return delete(division,"DELETE from division where division_id = ?");
    }

    @Override
    public Division modify(Division division) {
        return modify(division,"UPDATE division SET division_type = ?, league_type = ? WHERE division_id  = ?",
                division.getType().name(),division.league().name(),division.getId());
    }

    @Override
    public String getSql() {
        return "select * from division";
    }

    @Override
    public RowMapper<Division> getRowMapper() {
        return rowMapper;
    }

    protected PreparedStatementCreator getCreateStatement(final Division division, String sql) {
        return con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, division.league().name());
            ps.setString(2, division.getType().name());
            return ps;
        };
    }

    @Override
    public String getIdName() {
        return "division_id";
    }

    final static String CREATE = "INSERT INTO division(league_type,division_type) VALUES (?,?)";

}
