package com.society.leagues.dao;

import com.society.leagues.client.api.admin.SchedulerAdminApi;
import com.society.leagues.client.api.domain.Match;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SchedulerAdminDao extends Dao implements SchedulerAdminApi {
    @Autowired
    JdbcTemplate jdbcTemplate;
    private static Logger logger = LoggerFactory.getLogger(SchedulerAdminDao.class);

    @Override
    public List<Match> create(Integer seasonId, final List<Team> teams) {

        if (teams.size() % 2 == 1) {
            logger.info("Adding bye team");
            teams.add(Team.bye);
        }
        List<Match> matches = new ArrayList<>();

        for(int i = 1;i<teams.size();i++) {
            createOneRound(matches,teams,seasonId,i);
            // Move last item to first
            teams.add(1, teams.get(teams.size()-1));
            teams.remove(teams.size()-1);
        }
        return matches;
    }

    /**
     * Creates one round, i.e. a set of matches where each team plays once.
     *
     * @param round Round number.
     * @param list  List of teams
     */
    private List<Match> createOneRound(List<Match> matches, List<Team> teams,Integer seasonId, int round) {

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
            matches.add(new Match(t1,t2,seasonId));
        }
        //Make sure home/away is evenly split
        Map<String,Boolean> homeState = new HashMap<>();
        for (Match match : matches) {

        }
        return matches;
    }
}