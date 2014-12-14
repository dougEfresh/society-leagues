package com.society.leagues.api.player;

import com.society.leagues.domain.SocietyDao;
import com.society.leagues.domain.interfaces.Player;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PlayerDao extends SocietyDao {

    public Map<String,Object> getTeamHistory(Player player) {
        return getTeamHistory(player.getId());
    }

    public Map<String,Object> getTeamHistory(int id) {
        return queryForMap(TEAM_HISTORY,id);
    }

    public static String TEAM_HISTORY = " SELECT " +
                                                "team.name, team.team_id,\n" +
                                                "division.division_id,\n" +
                                                "CONCAT(league.league_type,' ',league.league_game,' - ',days.d_name) league_name,\n" +
                                                "season.season_number, season.season_year,\n" +
                                                "COUNT(result_ind.is_win) match_count,\n" +
                                                "SUM(result_ind.is_win) match_wins,\n" +
                                                "(COUNT(result_ind.is_win)-SUM(result_ind.is_win)) match_losses,\n" +
                                                "(ROUND(SUM(result_ind.is_win) / COUNT(result_ind.is_win),3)) percentage" +
                                                "\n" +
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
