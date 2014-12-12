<?php

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('enter_results_dialog.html');


$result = mysql_query("
SELECT *, DATE_FORMAT(match_start_date,'%M %D %Y') match_date
FROM match_schedule 
JOIN league ON league.league_id=match_schedule.league_id 
WHERE match_schedule.match_id='{$_GET['match_id']}'");

$row = mysql_fetch_assoc($result);
$league_id = $row['league_id'];

$view->assign('match_id', $_GET['match_id']);
$view->assign('league_description', $row['league_type'].' '.$row['league_game']);
$view->assign('match_date', $row['match_date']);

switch($league_id)
{
	case '1':
		$style = 'normal';
		$hc = "hc_9";
	break;	

	case '2':
		$style = 'normal';
		$hc = "hc_8";
	break;	
	
	case '3':
		$style = 'individual';
		$hc = "hc_straight";
	break;
	
	case '4':
		$style = 'mixed';
		$hc = "hc_m9";
	break;
	
	case '5':
		$style = 'individual';
		$hc = "hc_10";
	break;
		
	case '6':
		$style = 'normal';
		$hc = "hc_8Begin";
	break;
}




if (($league_id != '3') && ($league_id != '5'))
{
	$result = mysql_query("
	SELECT team.name, CONCAT(player.first_name,' ',player.last_name) player_name, player.player_id, team.team_id ht_id
	FROM match_schedule
	JOIN team ON team.team_id=match_schedule.home_team_id
	JOIN team_player ON team_player.tp_team=team.team_id AND team_player.tp_division=match_schedule.division_id
	JOIN player ON player.player_id=team_player.tp_player
	WHERE match_schedule.match_id='{$_GET['match_id']}'");

	while ($row = mysql_fetch_assoc($result))
	{
		foreach($row as $key => $val)
			$view->assign($key, $val);
		
		$view->parse("main.{$style}.home_player_1");
		$view->parse("main.{$style}.home_player_2");
	}


	$result = mysql_query("
	SELECT team.name, CONCAT(player.first_name,' ',player.last_name) player_name, player.player_id, team.team_id vt_id
	FROM match_schedule
	JOIN team ON team.team_id=match_schedule.visit_team_id
	JOIN team_player ON team_player.tp_team=team.team_id AND team_player.tp_division=match_schedule.division_id
	JOIN player ON player.player_id=team_player.tp_player
	WHERE match_schedule.match_id='{$_GET['match_id']}'");
	
	echo mysql_error();
	
	while ($row = mysql_fetch_assoc($result))	
	{
		foreach($row as $key => $val)
			$view->assign($key, $val);
		
		$view->parse("main.{$style}.visit_player_1");	
		$view->parse("main.{$style}.visit_player_2");	
	}	
}
else
{
	$result = mysql_query("
	SELECT CONCAT(player.first_name,' ',player.last_name) player_name, player.player_id hp1_id
	FROM match_schedule
	JOIN player ON player.player_id=match_schedule.home_player_id
	WHERE match_schedule.match_id='{$_GET['match_id']}'");
	
	$row = mysql_fetch_assoc($result);
	
	foreach($row as $key => $val)
		$view->assign($key, $val);

	$result = mysql_query("
	SELECT CONCAT(player.first_name,' ',player.last_name) player_name, player.player_id vp1_id
	FROM match_schedule
	JOIN player ON player.player_id=match_schedule.visit_player_id
	WHERE match_schedule.match_id='{$_GET['match_id']}'");
	
	$row = mysql_fetch_assoc($result);
	
	foreach($row as $key => $val)
		$view->assign($key, $val);
	
	$view->assign('match_id', $_GET['match_id']);	
	$view->parse("main.{$style}.players");	
}



$view->assign('match_id', $_GET['match_id']);	


if ($league_id == '4')
{
	$result = mysql_query("
	SELECT 
	h1.player_id hp1_id, CONCAT(hp1.first_name,' ',hp1.last_name) hp1_name, h1.player_handicap hp1_handicap, ht.team_id ht_id,
	h2.player_id hp2_id, CONCAT(hp2.first_name,' ',hp2.last_name) hp2_name, h2.player_handicap hp2_handicap, 
	v1.player_id vp1_id, CONCAT(vp1.first_name,' ',vp1.last_name) vp1_name, v1.player_handicap vp1_handicap, vt.team_id vt_id,
	v2.player_id vp2_id, CONCAT(vp2.first_name,' ',vp2.last_name) vp2_name, v2.player_handicap vp2_handicap, 
	h1.result_id h1_result_id, h2.result_id h2_result_id, v1.result_id v1_result_id, v2.result_id v2_result_id, 
	h1.match_id match_id, h1.match_number match_number,
	IF(h2.player_id IS NULL,'SINGLE','DOUBLE') is_double,
	IF(h1.is_win=1,'WIN','LOSS') h1_win,
	IF(h2.is_win IS NULL,'',IF(h2.is_win=1,'WIN','LOSS')) h2_win,
	IF(v1.is_win=1,'WIN','LOSS') v1_win,
	IF(v2.is_win IS NULL,'',IF(v2.is_win=1,'WIN','LOSS')) v2_win,
	IF(h1.result_id IS NULL,0,1) result_exists,
	h1.games_won h1_score, h2.games_won h2_score, v1.games_won v1_score, v2.games_won v2_score,
	ht.name ht_name, vt.name vt_name, ht.team_id ht_id, vt.team_id vt_id
	FROM result_ind h1
	JOIN player hp1 ON hp1.player_id=h1.player_id 
	JOIN team ht ON ht.team_id=h1.team_id
	LEFT JOIN result_ind h2 ON h2.match_id=h1.match_id AND h2.match_number=h1.match_number AND NOT h2.player_id=h1.player_id AND h2.team_id=h1.team_id
	LEFT JOIN player hp2 ON hp2.player_id=h2.player_id
	JOIN result_ind v1 ON v1.match_id=h1.match_id AND v1.match_number=h1.match_number AND NOT v1.team_id=h1.team_id
	JOIN player vp1 ON vp1.player_id=v1.player_id 
	JOIN team vt ON vt.team_id=v1.team_id
	LEFT JOIN result_ind v2 ON v2.match_id=h1.match_id AND v2.match_number=h1.match_number AND NOT v2.player_id=v1.player_id AND v2.team_id=v1.team_id
	LEFT JOIN player vp2 ON vp2.player_id=v2.player_id
	WHERE h1.match_id='{$_GET['match_id']}'
	GROUP BY h1.match_number ORDER BY match_number");
}
else if (($league_id == '3') || ($league_id == '5'))
{	
	$result = mysql_query("
	SELECT 
	h1.player_id hp1_id, CONCAT(hp1.first_name,' ',hp1.last_name) hp1_name, h1.player_handicap hp1_handicap,
	v1.player_id vp1_id, CONCAT(vp1.first_name,' ',vp1.last_name) vp1_name, v1.player_handicap vp1_handicap,
	h1.result_id h1_result_id, v1.result_id v1_result_id,
	h1.match_id match_id, h1.match_number match_number,
	IF(h1.is_win=1,'WIN','LOSS') h1_win,
	IF(v1.is_win=1,'WIN','LOSS') v1_win,
	IF(h1.result_id IS NULL,0,1) result_exists,
	hcd_h1.hcd_name hcdh_name, hcd_v1.hcd_name hcdv_name,
	h1.games_won h1_score, v1.games_won v1_score
	FROM match_schedule m
	
	LEFT JOIN result_ind h1 ON h1.match_id=m.match_id AND h1.player_id=m.home_player_id
	JOIN player hp1 ON hp1.player_id=m.home_player_id 
	LEFT JOIN handicap_display hcd_h1 ON (hcd_h1.hcd_id=hp1.{$hc})
	
	LEFT JOIN result_ind v1 ON v1.match_id=h1.match_id AND NOT v1.player_id=h1.player_id AND v1.match_number=h1.match_number
	JOIN player vp1 ON vp1.player_id=m.visit_player_id 
	LEFT JOIN handicap_display hcd_v1 ON (hcd_v1.hcd_id=vp1.{$hc})
	
	WHERE m.match_id='{$_GET['match_id']}'
	GROUP BY h1.match_number ORDER BY match_number");
}
else
{
	$result = mysql_query("
	SELECT 
	h1.match_id, h1.match_number,
	h1.player_id hp1_id, h1.result_id h1_result_id, h1.player_handicap hp1_handicap,
	IF(h1.is_win=1,'WIN','LOSS') h1_win, h1.games_won h1_score,
	v1.player_id vp1_id, v1.result_id v1_result_id, v1.player_handicap vp1_handicap,
	IF(v1.is_win=1,'WIN','LOSS') v1_win, v1.games_won v1_score,
	IF(h1.result_id IS NULL,0,1) result_exists,
	CONCAT(hp1.first_name,' ',hp1.last_name) hp1_name,
	CONCAT(vp1.first_name,' ',vp1.last_name) vp1_name,
	hcd_h1.hcd_name hcdh_name, hcd_v1.hcd_name hcdv_name,
	ht.name team_name, vt.name opponent_team_name, ht.team_id ht_id, vt.team_id vt_id
	FROM result_ind h1
	JOIN result_ind v1 ON v1.match_id=h1.match_id 
	AND v1.match_number=h1.match_number AND NOT v1.team_id=h1.team_id 
	JOIN player hp1 ON hp1.player_id=h1.player_id
	JOIN player vp1 ON vp1.player_id=v1.player_id
	JOIN team ht ON ht.team_id=h1.team_id
	JOIN team vt ON vt.team_id=v1.team_id
	
	LEFT JOIN handicap_display hcd_h1 ON (hcd_h1.hcd_id=hp1.{$hc})
	LEFT JOIN handicap_display hcd_v1 ON (hcd_v1.hcd_id=vp1.{$hc})
	
	RIGHT JOIN match_schedule m ON m.match_id=h1.match_id AND m.home_team_id=h1.team_id
	WHERE h1.match_id='{$_GET['match_id']}' ORDER BY match_number");
}

$view->assign('match_id', $_GET['match_id']);	

$home_score = $visitor_score = 0;
$game_count = mysql_num_rows($result);
$count = 1;

while ($row = mysql_fetch_assoc($result))
{
	foreach($row as $key => $val)
		$view->assign($key, $val);
	 
	$home_score += ($row['h1_score'] + $row['h2_score']);
	$visit_score += ($row['v1_score'] + $row['v2_score']);
	 
	$home_team = $row['ht_id'];
	
	if ($count == $game_count)
		$view->parse("main.{$style}.result.delete_button");
	else
		$view->parse("main.{$style}.result.no_delete_button");

	if ($row['result_exists'] == '1')
		$view->parse("main.{$style}.result");
	else
		$view->parse("main.{$style}.empty_result");
	
	$count++;
	
	$last_match = $row['match_number'];
}

$view->assign('home_score', $home_score);
$view->assign('visit_score', $visit_score);
$view->assign('match_id', $_GET['match_id']);	
$view->assign('match_count', $last_match);

$result = mysql_query("
SELECT 
result_team.team_id home_team, IF(result_team.is_win=1,'WIN','LOSS') home_win,
opponent_team.team_id visitor_team, IF(opponent_team.is_win=1,'WIN','LOSS') visit_win
FROM result_team
JOIN result_team opponent_team ON opponent_team.match_id=result_team.match_id AND NOT opponent_team.team_id=result_team.team_id
WHERE result_team.match_id='{$_GET['match_id']}' AND result_team.team_id='{$home_team}'");

$row = mysql_fetch_assoc($result);

$view->assign('home_result', $row['home_win']);
$view->assign('visit_result', $row['visit_win']);

$view->parse("main.{$style}");
$view->parse('main');
$view->out('main');
?>