package com.society.leagues.dao;

import com.society.leagues.domain.SocietyDao;
import com.society.leagues.domain.interfaces.Player;
import com.society.leagues.domain.player.PlayerDb;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PlayerDao extends SocietyDao {

    public List<Map<String,Object>> getTeamHistory(int id) {
        return queryForListMap(TEAM_HISTORY,id);
    }

    public Player getPlayer(String username, String password) {
        Map<String, Object> data = jdbcTemplate.queryForMap(
                "SELECT *," +
                        "case when g_name = 'Root' or g_name = 'Operator' then 1 else 0 end as admin" +
                        " From player p left join groups g on p.player_group=g_id " +
                        " WHERE p.player_login = ? " +
                        " AND p.`password` = ?",
                username,
                password
        );
        PlayerDb player = new PlayerDb();
        player.setFirstName((String) data.get("first_name"));
        player.setLastName((String) data.get("last_name"));
        player.setLogin((String) data.get("player_login"));
        player.setId((Integer) data.get("player_id"));
        player.setAdmin( ((Long) data.get("admin") == 1)) ;
        return player;
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
