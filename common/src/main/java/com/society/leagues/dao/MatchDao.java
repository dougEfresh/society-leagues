package com.society.leagues.dao;

import com.society.leagues.client.api.MatchApi;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.Division;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@Primary
public class MatchDao extends ClientDao<Match> implements MatchApi {
    @Autowired Dao dao;
    @Autowired TeamDao teamDao;
    @Autowired SeasonDao seasonDao;

    @Override
    public Match get(Integer id) {
        List<Match> matches = processResultSet("select * from team_match where match_id = ?", id);
        if (matches == null || matches.isEmpty())
            return null;
        return matches.get(0);
    }

    @Override
    public List<Match> current(List<User> users) {
        return null;
    }

    @Override
    public List<Match> current(Integer userId) {
        return null;
    }

    @Override
    public List<Match> past(List<User> user) {
        return null;
    }

    @Override
    public List<Match> past(Integer userId) {
        return null;
    }

    @Override
    public List<Match> all(List<User> user) {
        return null;
    }

    @Override
    public List<Match> all(Integer userId) {
        return null;
    }

    @Override
    public List<Match> get() {
        return processResultSet("select m.* from team_match m join season s on m.season_id = s.season_id where s.season_status = ?", Status.ACTIVE.name());
        //return processResultSet("select m.* from team_match m join season s on m.season_id = s.season_id ");
    }

    private List<Match> processResultSet(String sql, Object ...args) {
        List<Map<String,Object>> results = dao.get(sql,args);
        List<Match> matches = new ArrayList<>();

        if (results == null || results.isEmpty())
            return null;

        for (Map<String, Object> m : results) {
            Match match = new Match();
            Season season = seasonDao.get((Integer) m.get("season_id"));
            Team team = teamDao.get((Integer) m.get("home_team_id"));
            match.setId((Integer) m.get("team_match_id"));
            match.setSeason(season);
            match.setHome(team);
            team = teamDao.get((Integer) m.get("away_team_id"));
            match.setAway(team);
            match.setMatchDate((Date) m.get("match_date"));
            match.setWin((Integer) m.get("win"));
            match.setRacks((Integer) m.get("racks"));
            matches.add(match);
        }
        return matches;
    }

    @Override
    public RowMapper<Match> getRowMapper() {
        return null;
    }

}
