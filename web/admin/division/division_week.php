<?php
session_start();

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('division_week.html');

$result = mysql_query("
SELECT *, DATE_FORMAT(match_schedule.match_start_date, '%M %D') week,
ht.name home_team, vt.name visit_team, league.league_type, 
CONCAT(hp.first_name,' ',hp.last_name) home_player, 
CONCAT(vp.first_name,' ',vp.last_name) visit_player
FROM match_schedule
LEFT JOIN team ht ON ht.team_id=match_schedule.home_team_id
LEFT JOIN team vt ON vt.team_id=match_schedule.visit_team_id
LEFT JOIN player hp ON hp.player_id=match_schedule.home_player_id
LEFT JOIN player vp ON vp.player_id=match_schedule.visit_player_id
JOIN division ON division.division_id=match_schedule.division_id
JOIN league ON league.league_id=division.league_id 
WHERE match_schedule.division_id='{$_GET['division_id']}' AND match_schedule.match_number='{$_GET['week']}'");

while ($row = mysql_fetch_assoc($result))
{
	foreach($row as $key => $val)
		$view->assign($key, $val);
		
	$view->parse("main.{$row['league_type']}");
}

$view->parse('main');
$view->out('main');

?>