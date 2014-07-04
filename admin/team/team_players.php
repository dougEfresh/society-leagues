<?php

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('team_players.html');

$result = mysql_query("SELECT division.division_id, division_member.team_id, team.name, 
						CONCAT(player.first_name,' ',player.last_name) player_name, player.player_id,
						COUNT(player.player_id) matches, SUM(result_ind.is_win) wins,
						(ROUND(SUM(result_ind.is_win) / COUNT(player.player_id),3)) AS percentage
						FROM division
						LEFT JOIN season ON season.season_id=division.season_id
						LEFT JOIN division_member ON division_member.division_id=division.division_id
						LEFT JOIN team ON team.team_id=division_member.team_id
						LEFT JOIN match_schedule ON (match_schedule.season_id=season.season_id AND 
						(match_schedule.home_team_id=team.team_id OR match_schedule.visit_team_id=team.team_id))
						RIGHT JOIN result_ind ON (result_ind.match_id=match_schedule.match_id AND result_ind.team_id=team.team_id)
						RIGHT JOIN player ON player.player_id=result_ind.player_id
						WHERE team.team_id='{$_GET['team_id']}' AND division.division_id='{$_GET['division_id']}'
						AND NOT player.player_id IN (218,224)
						GROUP BY player.player_id");


while($row = mysql_fetch_assoc($result))
{
	foreach($row as $key => $val)
		$view->assign($key, $val);
	
	$view->parse('main.player');
}

$view->parse('main');
$view->out('main');

?>