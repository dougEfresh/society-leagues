<?php
session_start();

include '../include/mysql.php';
include '../include/xtemplate.class.php';

$view = new xtemplate('home_nav.html');

$result = mysql_query("
SELECT * FROM team_player 
JOIN team ON team.team_id=team_player.tp_team
JOIN player ON player.player_id='{$_SESSION['player_id']}'
WHERE tp_player='{$_SESSION['player_id']}'
GROUP BY team_id");


while ($row = mysql_fetch_assoc($result))
{
	foreach ($row as $key => $val)
		$view->assign($key, $val);
		
	$view->parse('main.team');
}

$result = mysql_query("
SELECT * FROM active_divisions 
JOIN division ON division.division_id=active_divisions.ad_division
JOIN league ON league.league_id=division.league_id
JOIN days ON d_id=division_day
JOIN season ON season.season_id=division.season_id
JOIN season_name ON sn_id=season.season_number");

while ($row = mysql_fetch_assoc($result))
{
	foreach ($row as $key => $val)
		$view->assign($key, $val);
		
	$view->parse('main.division');
}
$view->parse('main');
$view->out('main');

?>