package com.society.leagues.dao;

import com.society.leagues.client.api.PlayerClientApi;
import com.society.leagues.client.api.admin.PlayerAdminApi;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.Division;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PlayerDao extends Dao<Player> implements PlayerClientApi, PlayerAdminApi {
    @Autowired SeasonDao seasonDao;
    @Autowired TeamDao teamDao;
    @Autowired DivisionDao divisionDao;
    @Autowired TeamMatchDao teamMatchDao;
    @Autowired TeamResultDao teamResultDao;
    @Autowired ChallengeDao challengeDao;
    @Autowired UserDao userDao;
    @Autowired PlayerResultDao playerResultDao;


    RowMapper<Player> rowMapper = (rs, rowNum) -> {
        Season season = seasonDao.get(rs.getInt("season_id"));
        Team team = teamDao.get(rs.getInt("team_id"));
        Division division = divisionDao.get(rs.getInt("division_id"));

        Player player = new Player();
        player.setDivision(division);
        player.setHandicap(Handicap.values()[rs.getInt("handicap")]);
        player.setStart(rs.getDate("start_date"));
        player.setEnd(rs.getDate("end_date"));
        player.setTeam(team);
        player.setSeason(season);
        player.setUser(userDao.get(rs.getInt("user_id")));
        player.setId(rs.getInt("player_id"));
        return player;
    };

    public List<Player> getByUser(User user) {
        List<Player> players = get().stream().filter(p -> p.getUser().getId().equals(user.getId())).collect(Collectors.toList());
        return players;
    }

    @Override
    public Player create(final Player player) {
        return create(player, getCreateStatement(player));
    }

    @Override
    public Boolean delete(final Player player) {
        return delete(player, "update player set end_date = CURRENT_TIMESTAMP WHERE player_id = ?");
    }

    @Override
    public Player modify(final Player player) {
        return modify(player,MODIFY,
                player.getSeason().getId(),
                player.getUserId(),
                player.getTeam().getId(),
                player.getStart(),player.getEnd(),
                player.getId()
        );

    }

    protected PreparedStatementCreator getCreateStatement(final Player player) {
        return con -> {
            PreparedStatement ps = con.prepareStatement(CREATE, Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            ps.setInt(i++, player.getSeason().getId());
            ps.setInt(i++, player.getDivision().getId());
            ps.setInt(i++, player.getUserId());
            ps.setInt(i++, player.getTeam().getId());
            ps.setInt(i++, player.getHandicap().ordinal());

            if (player.getStart() != null)
                ps.setDate(i++, new java.sql.Date(player.getStart().getTime()));
            else
                ps.setDate(i++, null);

            if (player.getEnd() != null)
                ps.setDate(i++, new java.sql.Date(player.getEnd().getTime()));
            else
                ps.setDate(i++, null);

            return ps;
        };
    }

    static String CREATE = "INSERT INTO player " +
            "(" +
            "season_id," +
            "division_id," +
            "user_id," +
            "team_id," +
            "handicap," +
            "start_date," +
            "end_date) " +
            "VALUES " +
            "(?,?,?,?,?,?,?)";

    static String MODIFY = "UPDATE player " +
            "set " +
            "season_id=?," +
            "user_id=?," +
            "team_id=?," +
            "handicap=?," +
            "start_date=?," +
            "end_date=?," +
            " where player_id = ?";

    @Override
    public RowMapper<Player> getRowMapper() {
            return rowMapper;
    }

    @Override
    public String getIdName() {
        return "player_id";
    }

    @Override
    public String getSql() {
        return "select * from player";
    }
}
