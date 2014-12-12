<?php

include '../include/mysql.php';
include '../include/xtemplate.class.php';

$view = new xtemplate('opponents.html');
	
$result = mysql_query("
SELECT 
pt.name player_team, o_res.player_id opponent_id, CONCAT(player.first_name,' ',player.last_name) player_name, p_res.player_handicap player_handicap,
ot.name opponent_team, ot.team_id opponent_team_id, p_res.player_id player_id, CONCAT(op.first_name,' ',op.last_name) opponent_name, o_res.player_handicap opponent_handicap,
DATE_FORMAT(match_schedule.match_start_date,'%m-%d-%Y') match_date,
CONCAT(league.league_type,' ',league.league_game) league_game,
IF(p_res.is_win=1,'WIN','LOSS') outcome
FROM result_ind p_res
JOIN player ON player.player_id=p_res.player_id
JOIN team pt ON pt.team_id=p_res.team_id
JOIN result_ind o_res ON o_res.match_id=p_res.match_id AND o_res.match_number=p_res.match_number AND NOT o_res.player_id=p_res.player_id
JOIN player op ON op.player_id=o_res.player_id
JOIN team ot ON ot.team_id=o_res.team_id
JOIN match_schedule ON match_schedule.match_id=p_res.match_id
JOIN division ON division.division_id=match_schedule.division_id
JOIN league ON league.league_id=division.league_id
WHERE p_res.player_id='{$_GET['player_id']}'
ORDER BY p_res.match_id, p_res.match_number");

while($row = mysql_fetch_assoc($result))
{
	foreach($row as $key => $val)
		$view->assign($key, $val);
	
	$view->parse('main.opponent_history');
}
			
$view->parse('main');
$view->out('main');

?>