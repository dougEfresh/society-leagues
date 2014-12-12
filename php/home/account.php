<?php

include '../include/mysql.php';
include '../include/xtemplate.class.php';

$view = new xtemplate('account.html');

$result = mysql_query("SELECT *,
						hc_8B.hcd_name hc8B, 
						hc_8.hcd_name hc8, 
						hc_10.hcd_name hc10,
						hc_m8.hcd_name hcm8, 
						hc_s.hcd_name hcs, 
						hc_9.hcd_name hc9, 
						hc_m9.hcd_name hcm9
						FROM player 
						LEFT JOIN handicap_display hc_8B ON hc_8B.hcd_id=player.hc_8Begin
						LEFT JOIN handicap_display hc_8 ON hc_8.hcd_id=player.hc_8
						LEFT JOIN handicap_display hc_9 ON hc_9.hcd_id=player.hc_9
						LEFT JOIN handicap_display hc_s ON hc_s.hcd_id=player.hc_straight
						LEFT JOIN handicap_display hc_m9 ON hc_m9.hcd_id=player.hc_m9
						LEFT JOIN handicap_display hc_m8 ON hc_m8.hcd_id=player.hc_m8
						LEFT JOIN handicap_display hc_10 ON hc_10.hcd_id=player.hc_10
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