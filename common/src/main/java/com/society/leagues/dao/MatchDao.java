package com.society.leagues.dao;

import com.society.leagues.client.api.MatchApi;
import com.society.leagues.client.api.admin.MatchAdminApi;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.Division;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Component
public class MatchDao extends Dao<Match> implements MatchApi, MatchAdminApi {
    @Autowired SeasonDao seasonDao;
    @Autowired TeamDao teamDao;
    @Autowired DivisionDao divisionDao;

    @Override
    public String getSql() {
        return "select * from match";
    }

     public RowMapper<Match> rowMapper = (rs, rowNum) -> {
         Season season = seasonDao.get(rs.getInt("season_id"));
         Team team = teamDao.get(rs.getInt("team_id"));
         Division division = divisionDao.get(rs.getInt("division_id"));
         Match m = new Match();
         return m;
    };

    @Override
    public RowMapper<Match> getRowMapper() {
        return rowMapper;
    }

    @Override
    public Match create(Match match) {
        List<SqlParameter> parameters = Arrays.asList(
                new SqlParameter(Types.INTEGER),
                new SqlParameter(Types.INTEGER),
                new SqlParameter(Types.INTEGER),
                new SqlParameter(Types.DATE));
        PreparedStatementCreatorFactory ps = new PreparedStatementCreatorFactory(CREATE,parameters);
        ps.setReturnGeneratedKeys(true);
        return create(match, ps.newPreparedStatementCreator(new Object[]{
                match.getHome().getId(),
                match.getAway().getId(),
                match.getSeason().getId(),
                match.getMatchDate()
        }));
    }

    @Override
    public Match modify(Match match) {
        return modify(match, MODIFY,
                match.getSeason().getId(),
                match.getHome().getId(),
                match.getAway().getId(),
                match.getWin(),
                match.getId()
        );
    }

    /**
     * A around is a day
     * @param teams List of teams to match for a day
     * @param season The season
     * @param round the week round
     * @return list of matches
     */
    private List<Match> createRound(Map<Team,Boolean> homeState, List<Team> teams, Season season, int round) {
        List<Match> matches = new ArrayList<>();
        java.sql.Date d = (java.sql.Date) season.getStartDate();
        LocalDate matchDate= d.toLocalDate().plusDays((round-1)*7);
        //LocalDateTime matchDate = LocalDateTime.from(season.getStartDate().toInstant()).plusDays((round-1) * 7);

        int mid = teams.size() / 2;
        // Split list into two

        List<Team> l1 = new ArrayList<>();
        for (int j = 0; j < mid; j++) {
            l1.add(teams.get(j));
        }

        List<Team> l2 = new ArrayList<>();
        // We need to reverse the other list
        for (int j = teams.size() - 1; j >= mid; j--) {
            l2.add(teams.get(j));
        }

        for (int tId = 0; tId < l1.size(); tId++) {
            Team t1;
            Team t2;
            // Switch sides after each round
            if (round % 2 == 1) {
                t1 = l1.get(tId);
                t2 = l2.get(tId);
            } else {
                t1 =  l2.get(tId);
                t2 =  l1.get(tId);
            }

            //if (!isHome(homeState,t1) && !isHome(homeState,t2)) {
            //  homeState.put(t1,true);
            //homeState.put(t2,true);
            //matches.add(new Match(t1,t2,season,matchDate));

            if (isHome(homeState,t1)) {
                homeState.put(t1,false);
                matches.add(new Match(t2,t1,season, java.util.Date.from(matchDate.atStartOfDay(ZoneId.systemDefault()).toInstant())));
            } else {
                homeState.put(t2,false);
                matches.add(new Match(t1,t2,season, java.util.Date.from(matchDate.atStartOfDay(ZoneId.systemDefault()).toInstant())));
            }

        }
        return matches;
    }

    private Map<Team,Boolean> initialHomeState(List<Team> teams) {
        Map<Team,Boolean> homeState = new HashMap<>();
        for (int i = 0; i < teams.size(); i++ ) {
            homeState.put(teams.get(i),false);
            if (i % 2 == 0) {
                homeState.put(teams.get(i),true);
            }
        }
        return homeState;
    }

    boolean isHome(Map<Team,Boolean> homeState, Team team) {
        if (homeState.containsKey(team)) {
            return homeState.get(team);
        }

        return false;
    }

    public List<Match> create(Integer seasonId, final List<Team> teams) {
        try {
            Season season = null ;//seasonDao.get(seasonId);
            if (teams.size() % 2 == 1) {
                logger.info("Adding bye team");
                teams.add(Team.bye);
            }
            List<Match> matches = new ArrayList<>();
            Map<Team, Boolean> homeState = initialHomeState(teams);

            for (int i = 1; i <= season.getRounds(); i++) {
                matches.addAll(createRound(homeState, teams, season, i));
                // Move last item to first
                teams.add(1, teams.get(teams.size() - 1));
                teams.remove(teams.size() - 1);
            }
            Collections.sort(matches);
            return matches;
        } catch (Throwable t) {
            logger.error(t.getLocalizedMessage(),t);
        }
        return Collections.emptyList();
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
            "away_team_id=? , " +
            "win=? " +
            " where team_match_id = ?";

}
