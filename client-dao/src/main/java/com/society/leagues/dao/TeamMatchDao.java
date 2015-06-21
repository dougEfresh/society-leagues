package com.society.leagues.dao;

import com.society.leagues.client.api.TeamMatchApi;
import com.society.leagues.client.api.admin.MatchAdminApi;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.Division;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;


@Component
public class TeamMatchDao extends Dao<TeamMatch> implements TeamMatchApi, MatchAdminApi {
    @Autowired SeasonDao seasonDao;
    @Autowired TeamDao teamDao;
    @Autowired DivisionDao divisionDao;
    @Autowired PlayerDao playerDao;

    @Override
    public String getSql() {
        return "select * from team_match";
    }

     public RowMapper<TeamMatch> rowMapper = (rs, rowNum) -> {
         Season season = seasonDao.get(rs.getInt("season_id"));
         Team home = teamDao.get(rs.getInt("home_team_id"));
         Team away = teamDao.get(rs.getInt("away_team_id"));

         TeamMatch m = new TeamMatch();
         m.setMatchDate(rs.getTimestamp("match_date").toLocalDateTime());
         m.setAway(away);
         m.setHome(home);
         m.setSeason(season);
         m.setId(rs.getInt("team_match_id"));
         return m;
    };

    @Override
    public RowMapper<TeamMatch> getRowMapper() {
        return rowMapper;
    }

    @Override
    public TeamMatch create(TeamMatch teamMatch) {
        return create(teamMatch,getCreateStatement(teamMatch,CREATE));
    }

    @Override
    public TeamMatch modify(TeamMatch teamMatch) {
        return modify(teamMatch, MODIFY,
                teamMatch.getSeason().getId(),
                teamMatch.getHome().getId(),
                teamMatch.getAway().getId(),
                teamMatch.getId()
        );
    }

    private PreparedStatementCreator getCreateStatement(final TeamMatch teamMatch, String sql) {
        return con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            ps.setInt(i++, teamMatch.getHome().getId());
            ps.setInt(i++, teamMatch.getAway().getId());
            ps.setInt(i++, teamMatch.getSeason().getId());
            ps.setInt(i++, teamMatch.getDivision().getId());
            if (teamMatch.getMatchDate() == null) {
                ps.setTimestamp(i++, null);
            } else {
                ps.setTimestamp(i++, Timestamp.valueOf(teamMatch.getMatchDate()));
            }
            return ps;
        };
    }

    @Override
    public String getIdName() {
        return "team_match_id";
    }

    static String CREATE = "INSERT INTO team_match " +
            "(" +
            "home_team_id,away_team_id,season_id,match_date) " +
            "VALUES " +
            "(?,?,?,?)";

    static String MODIFY = "UPDATE team_match " +
            "set " +
            "season_id=? ," +
            "home_team_id=? , " +
            "away_team_id=? " +
            " where team_match_id = ?";
}
