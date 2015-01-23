package com.society.leagues.dao;

import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.Division;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlayerDao extends ClientDao<Player> {
        
    public RowMapper<Player> rowMapper = (rs, rowNum) -> {
        Season season = SeasonDao.rowMapper.mapRow(rs,rowNum);
        Team team = TeamDao.rowMapper.mapRow(rs,rowNum);
        User user = UserDao.rowMapper.mapRow(rs,rowNum);
        Division division = DivisionDao.rowMapper.mapRow(rs,rowNum);
        
        Player player = new Player();
        player.setDivision(division);
        player.setHandicap(Handicap.values()[rs.getInt("handicap")]);
        player.setStatus(Status.valueOf(rs.getString("player_status")));
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
        return null;
    }

    @Override
    public RowMapper<Player> getRowMapper() {
            return rowMapper;
    }
}
