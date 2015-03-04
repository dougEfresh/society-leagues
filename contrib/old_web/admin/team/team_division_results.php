<?php

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('team_division_results.html');

$result = mysql_query("SELECT 
match_schedule.match_id, match_schedule.match_number, 
home_team.name home_team, CONCAT(home_player.first_name,' ',home_player.last_name) home_player, 
IF(home_result.is_win=1,'WIN','LOSS') home_score, home_result.games_won home_points, home_result.player_handicap home_hcap,
visit_team.name visit_team, CONCAT(visit_player.first_name,' ',visit_player.last_name) visit_player, 
IF(visit_result.is_win=1,'WIN','LOSS') visit_score, visit_result.games_won visit_points, visit_result.player_handicap visit_hcap
FROM match_schedule 
RIGHT JOIN team home_team ON home_team.team_id=match_schedule.home_team_id
RIGHT JOIN team visit_team ON visit_team.team_id=match_schedule.visit_team_id
RIGHT JOIN result_ind home_result ON 
(home_result.match_id=match_schedule.match_id AND home_result.team_id=home_team.team_id)
RIGHT JOIN result_ind visit_result ON 
(visit_result.match_id=match_schedule.match_id AND visit_result.team_id=visit_team.team_id 
AND visit_result.match_number=home_result.match_number)
RIGHT JOIN player home_player ON home_player.player_id=home_result.player_id
RIGHT JOIN player visit_player ON visit_player.player_id=visit_result.player_id
WHERE division_id='{$_GET['division_id']}' AND 
(match_schedule.home_team_id='{$_GET['team_id']}' OR match_schedule.visit_team_id='{$_GET['team_id']}') 
AND NOT home_player.player_id IN (218,224)
ORDER BY match_schedule.match_number ;");

while($row = mysql_fetch_assoc($result))
{
	foreach($row as $key => $val)
		$view->assign($key, $val);
	
	$view->parse('main.match');
}

$view->parse('main');
$view->out('main');

?>