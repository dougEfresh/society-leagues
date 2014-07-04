<?php

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('player_division_results.html');

$result = mysql_query("SELECT * FROM division WHERE division_id='{$_GET['division_id']}'");

$row = mysql_fetch_assoc($result);

switch($row['league_id'])
{
	case '1':
		$hc = "(home_hc.hcd_league='{$row['league_id']}' AND home_hc.hcd_handicap=hplayer.handicap)";
		$vc = "(visit_hc.hcd_league='{$row['league_id']}' AND visit_hc.hcd_handicap=vplayer.handicap)";
	break;
	
	case '2':
		$hc = "(home_hc.hcd_league='{$row['league_id']}' AND home_hc.hcd_handicap=hplayer.handicap_eight)";
		$vc = "(visit_hc.hcd_league='{$row['league_id']}' AND visit_hc.hcd_handicap=vplayer.handicap_eight)";
	break;
	
	case '3':
		$hc = "(home_hc.hcd_league='{$row['league_id']}' AND home_hc.hcd_handicap=hplayer.handicap_straight)";
		$vc = "(visit_hc.hcd_league='{$row['league_id']}' AND visit_hc.hcd_handicap=vplayer.handicap_straight)";
	break;
	
	case '4':
		$hc = "(home_hc.hcd_league='{$row['league_id']}' AND home_hc.hcd_handicap=hplayer.handicap_mixed_9)";
		$vc = "(visit_hc.hcd_league='{$row['league_id']}' AND visit_hc.hcd_handicap=vplayer.handicap_mixed_9)";
	break;

	case '5':
		$hc = "(home_hc.hcd_league='{$row['league_id']}' AND home_hc.hcd_handicap=hplayer.handicap_10)";
		$vc = "(visit_hc.hcd_league='{$row['league_id']}' AND visit_hc.hcd_handicap=vplayer.handicap_10)";
	break;
		
	case '6':
		$hc = "(home_hc.hcd_league='{$row['league_id']}' AND home_hc.hcd_handicap=hplayer.handicap_eight_beginner)";
		$vc = "(visit_hc.hcd_league='{$row['league_id']}' AND visit_hc.hcd_handicap=vplayer.handicap_eight_beginner)";
	break;	
}

echo $hc;

$result = mysql_query(
"SELECT

hteam.name home_team, hteam.team_id, h_res.player_handicap h_hcap, home_hc.hcd_name home_hcap, visit_hc.hcd_name visit_hcap,
hplayer.player_id, CONCAT(hplayer.first_name,' ',hplayer.last_name) home_player,
IF(h_res.is_win=1,'W','L') h_result, h_res.games_won h_points,

vteam.name visit_team, vteam.team_id, v_res.player_handicap v_hcap,
vplayer.player_id, CONCAT(vplayer.first_name,' ',vplayer.last_name) visit_player,
IF(v_res.is_win=1,'W','L') v_result, v_res.games_won v_points,

division.division_id, h_res.is_win, v_res.is_win,
IF(match_schedule.home_team_id=hteam.team_id,'','') location


FROM result_ind h_res
RIGHT JOIN match_schedule ON match_schedule.match_id=h_res.match_id
RIGHT JOIN division ON division.division_id=match_schedule.division_id

RIGHT JOIN player hplayer ON hplayer.player_id=h_res.player_id
LEFT JOIN team hteam ON hteam.team_id=h_res.team_id

RIGHT JOIN result_ind v_res ON (v_res.match_id=h_res.match_id 
AND v_res.match_number=h_res.match_number AND NOT v_res.player_id=hplayer.player_id)
RIGHT JOIN player vplayer ON vplayer.player_id=v_res.player_id
LEFT JOIN team vteam ON vteam.team_id=v_res.team_id

LEFT JOIN handicap_display home_hc ON {$hc}
LEFT JOIN handicap_display visit_hc ON {$vc}
						
WHERE h_res.player_id='{$_GET['player_id']}' AND division.division_id='{$_GET['division_id']}' 
ORDER BY match_schedule.match_start_date;");

echo mysql_error();
while($row = mysql_fetch_assoc($result))
{
	foreach($row as $key => $val)
		$view->assign($key, $val);
	
	$view->parse('main.match');
}

$view->parse('main');
$view->out('main');

?>