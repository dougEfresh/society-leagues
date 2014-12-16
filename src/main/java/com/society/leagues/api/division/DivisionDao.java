package com.society.leagues.api.division;

import com.society.leagues.domain.SocietyDao;
import com.society.leagues.domain.interfaces.Player;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class DivisionDao extends SocietyDao {

    public List<Map<String, Object>> getInfo(int id) {
        return queryForListMap(DIV_LIST + " WHERE division.division_id=? "
                ,id);
    }

    public List<Map<String, Object>> getInfo(Player player) {
        return queryForListMap(DIV_LIST_PLAYER, player.getId());
    }

    public List<Map<String,Object>> getStandings(int id) {
        //TODO Fix the bloody number of params
        if (id == 1)
            return queryForListMap(DIV_STANDINGS + " ORDER BY team_wins DESC, player_match_wins DESC, player_match_losses ASC, pct DESC",
                    id, id, id, id, id, id, id, id);
        else if (id == 4)
            return queryForListMap(DIV_STANDINGS + " ORDER BY team_wins DESC, pct DESC, player_match_wins DESC, player_games_won DESC",
                    id, id, id, id, id, id, id, id);
        else
            return queryForListMap(DIV_STANDINGS + " ORDER BY team_wins DESC, pct DESC, player_match_wins DESC, player_games_won DESC",
                    id, id, id, id, id, id, id, id);
    }

    public List<Map<String,Object>> getList() {
        return queryForListMap(DIV_LIST);
    }

    public List<Map<String,Object>> getStandingsWeek(int id, int week) {
        return queryForListMap(DIV_WEEK_STANDINGS,id,week);
    }

    static String DIV_WEEK_STANDINGS = "SELECT ht.team_id, vt.team_id, \n" +
            "DATE_FORMAT(match_schedule.match_start_date, '%M %D') week,\n" +
            "ht.name home_team, vt.name visit_team, league.league_type, \n" +
            "CONCAT(hp.first_name,' ',hp.last_name) home_player, \n" +
            "CONCAT(vp.first_name,' ',vp.last_name) visit_player,\n" +
            "IF(hr.is_win=1,'W','L') home_res, IF(vr.is_win=1,'W','L') visit_res,\n" +
            "SUM(hi.is_win) home_points, SUM(vi.is_win) visit_points,\n" +
            "IF(hr.result_id IS NULL,' ',IF(hr.is_win=1,'W','L')) home_res,\n" +
            "IF(vr.result_id IS NULL,' ',IF(vr.is_win=1,'W','L')) visit_res\n" +
            "\n" +
            "FROM match_schedule\n" +
            "LEFT JOIN team ht ON ht.team_id=match_schedule.home_team_id\n" +
            "LEFT JOIN team vt ON vt.team_id=match_schedule.visit_team_id\n" +
            "LEFT JOIN result_team hr ON (hr.match_id=match_schedule.match_id AND match_schedule.home_team_id=hr.team_id)\n" +
            "LEFT JOIN result_team vr ON (vr.match_id=match_schedule.match_id AND match_schedule.visit_team_id=vr.team_id)\n" +
            "LEFT JOIN result_ind hi ON hi.match_id=match_schedule.match_id AND hi.team_id=ht.team_id\n" +
            "LEFT JOIN result_ind vi ON vi.match_id=match_schedule.match_id AND vi.team_id=vt.team_id AND vi.match_number=hi.match_number\n" +
            "LEFT JOIN player hp ON hp.player_id=match_schedule.home_player_id\n" +
            "LEFT JOIN player vp ON vp.player_id=match_schedule.visit_player_id\n" +
            "JOIN division ON division.division_id=match_schedule.division_id\n" +
            "JOIN league ON league.league_id=division.league_id \n" +
            "WHERE match_schedule.division_id=? AND match_schedule.match_number=?\n" +
            "GROUP BY match_schedule.match_id";

    static String DIV_LIST = "SELECT *,division.start_date jq_start, division.end_date jq_end \n" +
            " FROM division\n" +
            " RIGHT JOIN league ON league.league_id=division.league_id\n" +
            " RIGHT JOIN season ON season.season_id=division.season_id\n" +
            " RIGHT JOIN days ON  d_id=division.division_day\n" +
            " RIGHT JOIN season_name ON season_name.sn_id=season.season_number\n";

   static String DIV_LIST_PLAYER = "SELECT *,division.start_date jq_start, division.end_date jq_end \n" +
            " FROM division d\n" +
            " RIGHT JOIN league ON league.league_id=division.league_id\n" +
            " RIGHT JOIN season ON season.season_id=division.season_id\n" +
            " RIGHT JOIN days ON  d_id=division.division_day\n" +
            " RIGHT JOIN season_name ON season_name.sn_id=season.season_number\n" +
           "  JOIN team_player tp on d.division_id = tp.to_division where tp_player = ?";

    static String DIV_MATCH_IDS = "SELECT distinct match_id cache FROM match_schedule WHERE division_id=?";

    static String DIV_STANDINGS = "SELECT team.name, team.team_id,\n" +
            "SUM(result_team.is_win) team_wins, SUM(IF(result_team.is_win=1,0,1)) team_losses,\n" +
            "(SELECT SUM(is_win) FROM result_ind WHERE result_ind.team_id=division_member.team_id AND result_ind.match_id IN ( " + DIV_MATCH_IDS + ") ) player_match_wins,\n" +
            "(SELECT SUM(IF(is_win=1,0,1)) FROM result_ind WHERE result_ind.team_id=division_member.team_id AND result_ind.match_id IN (" + DIV_MATCH_IDS + ") ) player_match_losses,\n" +
            "(SELECT SUM(games_won) FROM result_ind WHERE result_ind.team_id=division_member.team_id AND result_ind.match_id IN (" + DIV_MATCH_IDS + ") ) player_games_won,\n" +
            "(SELECT SUM(games_lost) FROM result_ind WHERE result_ind.team_id=division_member.team_id AND result_ind.match_id IN (" + DIV_MATCH_IDS + ") ) player_games_lost,\n" +
            "\nTRIM(LEADING '0' FROM\n" +
            "FORMAT((SELECT SUM(games_won) FROM result_ind WHERE result_ind.team_id=division_member.team_id AND result_ind.match_id IN ("+ DIV_MATCH_IDS +")) /\n" +
            "((SELECT SUM(games_won) FROM result_ind WHERE result_ind.team_id=division_member.team_id AND result_ind.match_id IN (" + DIV_MATCH_IDS + ")) +\n" +
            "(SELECT SUM(games_lost) FROM result_ind WHERE result_ind.team_id=division_member.team_id AND result_ind.match_id IN (" + DIV_MATCH_IDS + "))),3)) pct" +
            "" +
            "\n" +
            "FROM division_member \n" +
            "JOIN team ON team.team_id=division_member.team_id\n" +
            "JOIN match_schedule ON match_schedule.division_id=division_member.division_id AND (match_schedule.home_team_id=division_member.team_id OR match_schedule.visit_team_id=division_member.team_id)\n" +
            "JOIN result_team ON result_team.team_id=division_member.team_id AND result_team.match_id=match_schedule.match_id\n" +
            "WHERE division_member.division_id=?\n" +
            "GROUP BY division_member.team_id ";
}
