package com.society.leagues.dao;

import com.society.leagues.client.admin.api.DivisionAdminApi;
import com.society.leagues.client.api.domain.division.Division;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Component
public class DivisionAdminDao extends Dao implements DivisionAdminApi {
    private static Logger logger = LoggerFactory.getLogger(DivisionAdminDao.class);

    @Override
    public Division create(Division division) {
         try {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(getCreateStatement(division),keyHolder);
            division.setId(keyHolder.getKey().intValue());
        } catch (Throwable t) {
            logger.error(t.getMessage(),t);
            return null;
        }
        return division;
    }

    @Override
    public Boolean delete(Division division) {
          try {
            return jdbcTemplate.update("DELETE from division where division_id = ?",division.getId()) > 0;
        } catch (Throwable t) {
            logger.error(t.getMessage(),t);
        }
        return Boolean.FALSE;
    }

    @Override
    public Division modify(Division division) {
          try {
            if (jdbcTemplate.update("UPDATE division SET `type` = ?, league_id = ? WHERE division_id  = ?",
                    division.getType().name(),division.getLeague().getId(),division.getId()) <= 0)
                return null;

            return division;
        } catch (Throwable t) {
            logger.error(t.getMessage(),t);
        }
        return null;
    }

    private PreparedStatementCreator getCreateStatement(final Division division) {
        return con -> {
            PreparedStatement ps = con.prepareStatement(CREATE, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, division.getLeague().getId());
            ps.setString(2, division.getType().name());
            return ps;
        };
    }

    final static String CREATE = "INSERT INTO division(league_id,`type`) VALUES (?,?)";
}

