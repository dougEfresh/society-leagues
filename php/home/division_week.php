<?php
session_start();

include '../include/mysql.php';
include '../include/xtemplate.class.php';

$view = new xtemplate('division_week.html');

$result = mysql_query("
SELECT ht.team_id, vt.team_id, 
DATE_FORMAT(match_schedule.match_start_date, '%M %D') week,
ht.name home_team, vt.name visit_team, league.league_type, 
CONCAT(hp.first_name,' ',hp.last_name) home_player, 
CONCAT(vp.first_name,' ',vp.last_name) visit_player,
IF(hr.is_win=1,'W','L') home_res, IF(vr.is_win=1,'W','L') visit_res,
SUM(hi.is_win) home_points, SUM(vi.is_win) visit_points,
IF(hr.result_id IS NULL,' ',IF(hr.is_win=1,'W','L')) home_res,
IF(vr.result_id IS NULL,' ',IF(vr.is_win=1,'W','L')) visit_res

FROM match_schedule
LEFT JOIN team ht ON ht.team_id=match_schedule.home_team_id
LEFT JOIN team vt ON vt.team_id=match_schedule.visit_team_id
LEFT JOIN result_team hr ON (hr.match_id=match_schedule.match_id AND match_schedule.home_team_id=hr.team_id)
LEFT JOIN result_team vr ON (vr.match_id=match_schedule.match_id AND match_schedule.visit_team_id=vr.team_id)
LEFT JOIN result_ind hi ON hi.match_id=match_schedule.match_id AND hi.team_id=ht.team_id
LEFT JOIN result_ind vi ON vi.match_id=match_schedule.match_id AND vi.team_id=vt.team_id AND vi.match_number=hi.match_number
LEFT JOIN player hp ON hp.player_id=match_schedule.home_player_id
LEFT JOIN player vp ON vp.player_id=match_schedule.visit_player_id
JOIN division ON division.division_id=match_schedule.division_id
JOIN league ON league.league_id=division.league_id 
WHERE match_schedule.division_id='{$_GET['division_id']}' AND match_schedule.match_number='{$_GET['week']}'
GROUP BY match_schedule.match_id");

echo mysql_error();

while ($row = mysql_fetch_assoc($result))
{
	foreach($row as $key => $val)
		$view->assign($key, $val);
		
	$view->parse("main.{$row['league_type']}");
}

$view->parse('main');
$view->out('main');

?>