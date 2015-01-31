package com.society.leagues.dao;

import com.society.leagues.client.api.PlayerClientApi;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.Division;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Primary
public class PlayerDao extends ClientDao<Player> implements PlayerClientApi {

    public RowMapper<Player> rowMapper = (rs, rowNum) -> {
        Season season = SeasonDao.rowMapper.mapRow(rs,rowNum);
        Team team = TeamDao.rowMapper.mapRow(rs,rowNum);
        User user = UserDao.rowMapper.mapRow(rs,rowNum);
        Division division = DivisionDao.rowMapper.mapRow(rs,rowNum);
        
        Player player = new Player();
        player.setDivision(division);
        player.setHandicap(Handicap.values()[rs.getInt("handicap")]);
        player.setStart(rs.getDate("start_date"));
        player.setEnd(rs.getDate("end_date"));
        player.setTeam(team);
        player.setSeason(season);
        player.setUser(user);
        player.setId(rs.getInt("player_id"));
        return player;
    };

    @Override
    public List<Player> get() {
        return list(CLIENT_REQUEST);
    }

    @Override
    public Player get(Integer id) {
        return get(id,CLIENT_REQUEST);
    }

    @Override
    public List<Player> findHandicapRange(Division division, Integer to, Integer from) {
        return list(CLIENT_REQUEST +
                            " where d.division_id = ? and handicap between ? and ?",
                    division.getId(),to,from);
    }

    @Override
    public RowMapper<Player> getRowMapper() {
            return rowMapper;
    }
}
