<?php

include '../include/mysql.php';
include '../include/xtemplate.class.php';

$view = new xtemplate('account.html');

$result = mysql_query("SELECT *,
						hcb.hcd_name hcb, hct.hcd_name hct, hcm.hcd_name hcm, 
						hcs.hcd_name hcs, hc8.hcd_name hc8, hc9.hcd_name hc9
						FROM player 
						JOIN handicap_display hc9 ON hc9.hcd_league='1' AND hc9.hcd_handicap=player.handicap
						JOIN handicap_display hc8 ON hc8.hcd_league='2' AND hc8.hcd_handicap=player.handicap_eight
						JOIN handicap_display hcs ON hcs.hcd_league='3' AND hcs.hcd_handicap=player.handicap_straight
						JOIN handicap_display hcm ON hcm.hcd_league='4' AND hcm.hcd_handicap=player.handicap_mixed_9
						JOIN handicap_display hct ON hct.hcd_league='5' AND hct.hcd_handicap=player.handicap_10
						JOIN handicap_display hcb ON hcb.hcd_league='6' AND hcb.hcd_handicap=player.handicap_eight_beginner
						WHERE player_id='{$_GET['player_id']}'");

$row = mysql_fetch_assoc($result);

foreach ($row as $key => $val)
	$view->assign($key, $val);
	
$result = mysql_query("SELECT
						team.name, team.team_id,
						division.division_id,
						CONCAT(league.league_type,' ',league.league_game,' - ',days.d_name) league_name,
						season.season_number, season.season_year,
						COUNT(result_ind.is_win) match_count,
						SUM(result_ind.is_win) match_wins,
						(COUNT(result_ind.is_win)-SUM(result_ind.is_win)) match_losses,
						(ROUND(SUM(result_ind.is_win) / COUNT(result_ind.is_win),3)) percentage
						FROM result_ind
						RIGHT JOIN match_schedule ON match_schedule.match_id=result_ind.match_id
						RIGHT JOIN division ON division.division_id=match_schedule.division_id
						RIGHT JOIN season ON season.season_id=division.season_id
						RIGHT JOIN days ON days.d_id=division.division_day
						RIGHT JOIN league ON league.league_id=division.league_id
						RIGHT JOIN player ON player.player_id=result_ind.player_id
						RIGHT JOIN team ON team.team_id=result_ind.team_id
						WHERE result_ind.player_id='{$_GET['player_id']}' GROUP BY division.division_id;");

while($row = mysql_fetch_assoc($result))
{
	foreach($row as $key => $val)
		$view->assign($key, $val);
	
	$view->parse('main.user_history');
}
			
$view->parse('main');
$view->out('main');

?>