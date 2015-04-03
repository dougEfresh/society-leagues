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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

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
         Division division = divisionDao.get(rs.getInt("division_id"));

         TeamMatch m = new TeamMatch();
         m.setMatchDate(rs.getTimestamp("match_date"));
         m.setAway(away);
         m.setHome(home);
         m.setSeason(season);
         m.setDivision(division);
         m.setId(rs.getInt("team_match_id"));
         m.setPlayersHome(playerDao.findHomeTeamPlayers(m));
         m.setPlayersAway(playerDao.findAwayTeamPlayers(m));
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


    /**
     * A around is a day
     * @param teams List of teams to match for a day
     * @param season The season
     * @param round the week round
     * @return list of matches
     */
    private List<TeamMatch> createRound(Map<Team,Boolean> homeState, List<Team> teams, Season season, int round) {
        List<TeamMatch> teamMatches = new ArrayList<>();
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
                teamMatches.add(new TeamMatch(t2,t1,season, java.util.Date.from(matchDate.atStartOfDay(ZoneId.systemDefault()).toInstant())));
            } else {
                homeState.put(t2,false);
                teamMatches.add(new TeamMatch(t1,t2,season, java.util.Date.from(matchDate.atStartOfDay(ZoneId.systemDefault()).toInstant())));
            }

        }
        return teamMatches;
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

    private boolean isHome(Map<Team,Boolean> homeState, Team team) {
        if (homeState.containsKey(team)) {
            return homeState.get(team);
        }

        return false;
    }

    private List<TeamMatch> create(Integer seasonId, final List<Team> teams) {
        try {
            Season season = null ;//seasonDao.get(seasonId);
            if (teams.size() % 2 == 1) {
                logger.info("Adding bye team");
                teams.add(Team.bye);
            }
            List<TeamMatch> teamMatches = new ArrayList<>();
            Map<Team, Boolean> homeState = initialHomeState(teams);

            for (int i = 1; i <= season.getRounds(); i++) {
                teamMatches.addAll(createRound(homeState, teams, season, i));
                // Move last item to first
                teams.add(1, teams.get(teams.size() - 1));
                teams.remove(teams.size() - 1);
            }
            Collections.sort(teamMatches);
            return teamMatches;
        } catch (Throwable t) {
            logger.error(t.getLocalizedMessage(),t);
        }
        return Collections.emptyList();
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
                ps.setDate(i++,null);
            } else {
                ps.setDate(i,new java.sql.Date(teamMatch.getMatchDate().getTime()));
            }
            return ps;
        };
    }
    static String CREATE = "INSERT INTO team_match " +
            "(" +
            "home_team_id,away_team_id,season_id,division_id,match_date) " +
            "VALUES " +
            "(?,?,?,?,?)";

    static String MODIFY = "UPDATE team_match " +
            "set " +
            "season_id=? ," +
            "home_team_id=? , " +
            "away_team_id=? " +
            " where team_match_id = ?";
}
