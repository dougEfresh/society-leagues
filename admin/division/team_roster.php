<?php

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('team_roster.html');

$result = mysql_query("SELECT * FROM division 
						JOIN league ON league.league_id=division.league_id
						WHERE division_id='{$_GET['division_id']}'");

$row = mysql_fetch_assoc($result);

switch($row['league_id'])
{
	case '1':
		$hc = "handicap";
	break;
	
	case '2':
		$hc = "handicap_eight";
	break;
	
	case '3':
		$hc = "handicap_straight";
	break;
	
	case '4':
		$hc = "handicap_mixed_9";
	break;
	
	case '5':
		$hc = "handicap_10";
	break;
	
	case '6':
		$hc = "handicap_eight_beginner";
	break;
}

$result = mysql_query("
SELECT CONCAT(player.first_name,' ',player.last_name) player_name, player.player_id,
team.name,
CONCAT('$',FORMAT(IF(dp_id IS NULL,d_amount,d_amount - SUM(dp_payment)),2)) amount,
COUNT(result_ind.result_id) games,
SUM(result_ind.is_win) wins, (COUNT(result_ind.result_id) - SUM(result_ind.is_win)) losses,
(ROUND(SUM(result_ind.is_win) / COUNT(result_ind.result_id),3)) AS percentage,
handicap_display.*
FROM team_player
RIGHT JOIN player ON player.player_id=team_player.tp_player
RIGHT JOIN team ON team.team_id=team_player.tp_team
LEFT JOIN division ON division.division_id='{$_GET['division_id']}'
LEFT JOIN handicap_display ON (hcd_league=division.league_id AND hcd_handicap={$hc})
LEFT JOIN match_schedule ON (match_schedule.division_id=division.division_id AND 
(match_schedule.home_team_id=team_player.tp_team OR match_schedule.visit_team_id=team_player.tp_team))
LEFT JOIN result_ind ON result_ind.match_id=match_schedule.match_id AND result_ind.player_id=player.player_id
LEFT JOIN division_dues ON d_division=tp_division AND d_player=tp_player AND d_team=tp_team
LEFT JOIN division_payment ON dp_division_dues=d_id
WHERE team_player.tp_team='{$_GET['team_id']}' AND team_player.tp_division='{$_GET['division_id']}'
GROUP BY tp_player ORDER BY percentage DESC");

echo mysql_error();

while($row = mysql_fetch_assoc($result))
{
	foreach($row as $key => $val)
		$view->assign($key, $val);
	
	$view->parse('main.player');
}

$view->parse('main');
$view->out('main');

?>