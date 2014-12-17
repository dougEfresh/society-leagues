package com.society.leagues.api.player;

import com.society.leagues.domain.SocietyDao;
import com.society.leagues.domain.interfaces.Player;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PlayerDao extends SocietyDao {

    public List<Map<String,Object>> getTeamHistory(int id) {
        return queryForListMap(TEAM_HISTORY,id);
    }

    public static String TEAM_HISTORY = " SELECT " +
                                                "team.name, team.team_id," +
                                                "division.division_id," +
                                                "CONCAT(league.league_type,' ',league.league_game,' - ',days.d_name) league_name," +
                                                "season.season_number, season.season_year," +
                                                "COUNT(result_ind.is_win) match_count," +
                                                "SUM(result_ind.is_win) match_wins," +
                                                "(COUNT(result_ind.is_win)-SUM(result_ind.is_win)) match_losses," +
                                                "(ROUND(SUM(result_ind.is_win) / COUNT(result_ind.is_win),3)) percentage" +
                                                " FROM result_ind " +
                                                " RIGHT JOIN match_schedule ON match_schedule.match_id=result_ind.match_id" +
                                                " RIGHT JOIN division ON division.division_id=match_schedule.division_id" +
                                                " RIGHT JOIN season ON season.season_id=division.season_id" +
                                                " RIGHT JOIN days ON days.d_id=division.division_day" +
                                                " RIGHT JOIN league ON league.league_id=division.league_id" +
                                                " RIGHT JOIN player ON player.player_id=result_ind.player_id" +
                                                " RIGHT JOIN team ON team.team_id=result_ind.team_id" +
                                                " WHERE result_ind.player_id=? " +
                                                " GROUP BY division.division_id;";
}
