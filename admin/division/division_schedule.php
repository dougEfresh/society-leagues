<?php

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('division_schedule.html');

$result = mysql_query(
"SELECT
division.division_id, league.league_type,
match_schedule.match_id, DATE_FORMAT(match_schedule.match_start_date,'%M %D %Y') match_date,
ht.name home_team, CONCAT(hp.first_name,' ',hp.last_name,'  (',hp.player_id,')') home_player,
IF(hir.is_win IS NULL, 'NA', IF(hir.is_win,'WIN','LOSS')) home_player_result,
IF(hp.player_id IS NULL, 'NA', hir.games_won) home_player_games_won,
IF(hr.is_win IS NULL, 'NA', IF(hr.is_win,'WIN','LOSS')) home_team_result,
SUM(hs.is_win) home_score,
vt.name visit_team, CONCAT(vp.first_name,' ',vp.last_name,'  (',vp.player_id,')') visit_player, 
IF(vir.is_win IS NULL, 'NA', IF(vir.is_win,'WIN','LOSS')) visit_player_result,
IF(vp.player_id IS NULL, 'NA', vir.games_won) visit_player_games_won,
IF(vr.is_win IS NULL, 'NA', IF(vr.is_win,'WIN','LOSS')) visit_team_result,
SUM(vs.is_win) visit_score
FROM division
RIGHT JOIN match_schedule ON match_schedule.division_id=division.division_id
RIGHT JOIN league ON league.league_id=division.league_id
LEFT JOIN player hp ON hp.player_id=match_schedule.home_player_id
LEFT JOIN result_ind hir ON (hir.player_id=hp.player_id AND hir.match_id=match_schedule.match_id)
LEFT JOIN team ht ON ht.team_id=match_schedule.home_team_id
LEFT JOIN result_team hr ON (hr.team_id=ht.team_id AND hr.match_id=match_schedule.match_id)
LEFT JOIN result_ind hs ON (hs.team_id=ht.team_id AND hs.match_id=match_schedule.match_id)
LEFT JOIN player vp ON vp.player_id=match_schedule.visit_player_id
LEFT JOIN result_ind vir ON (vir.player_id=vp.player_id AND vir.match_id=match_schedule.match_id)
LEFT JOIN team vt ON vt.team_id=match_schedule.visit_team_id
LEFT JOIN result_team vr ON (vr.team_id=vt.team_id AND vr.match_id=match_schedule.match_id)
LEFT JOIN result_ind vs ON (vs.team_id=vt.team_id AND vs.match_id=match_schedule.match_id AND vs.match_number=hs.match_number)
WHERE division.division_id='{$_GET['division_id']}' GROUP BY match_schedule.match_id  ORDER BY match_schedule.match_start_date");

while($row = mysql_fetch_assoc($result))
{
	if ( ($row['match_date'] != $last_date) && ($started))
		$view->parse("main.week");
		
	foreach($row as $key => $val)
		$view->assign($key, $val);
	
	$view->parse("main.week.{$row['league_type']}");
		
	$last_date = $row['match_date'];
	$started = true;	
	
	$playoffs = $row['playoffs'];
}

$view->parse('main');
$view->out('main');

?>