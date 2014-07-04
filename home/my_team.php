<?php

include '../include/mysql.php';
include '../include/xtemplate.class.php';

$view = new xtemplate('my_team.html');

$result = mysql_query("SELECT division.division_id, division_member.team_id, team.name, division.league_id,
						CONCAT(player.first_name,' ',player.last_name) player_name, player.player_id,
						COUNT(player.player_id) matches, SUM(result_ind.is_win) wins,
						(ROUND(SUM(result_ind.is_win) / COUNT(player.player_id),3)) AS percentage,
						(COUNT(player.player_id) - SUM(result_ind.is_win)) losses
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
						GROUP BY player.player_id ORDER BY percentage DESC");

while($row = mysql_fetch_assoc($result))
{
	foreach($row as $key => $val)
		$view->assign($key, $val);
	
	$league = $row['league_id'];
	
	$view->parse('main.player');
}

//echo $league;

switch($league)
{
	case '1':
		$hc = "(home_hc.hcd_league='{$row['league_id']}' AND home_hc.hcd_handicap=home_player.handicap)";
		$vc = "(visit_hc.hcd_league='{$row['league_id']}' AND visit_hc.hcd_handicap=visit_player.handicap)";
	break;
	
	case '2':
		$hc = "(home_hc.hcd_league='{$row['league_id']}' AND home_hc.hcd_handicap=home_player.handicap_eight)";
		$vc = "(visit_hc.hcd_league='{$row['league_id']}' AND visit_hc.hcd_handicap=visit_player.handicap_eight)";
	break;
	
	case '3':
		$hc = "(home_hc.hcd_league='{$row['league_id']}' AND home_hc.hcd_handicap=home_player.handicap_straight)";
		$vc = "(visit_hc.hcd_league='{$row['league_id']}' AND visit_hc.hcd_handicap=visit_player.handicap_straight)";
	break;
	
	case '4':
		$hc = "(home_hc.hcd_league='{$row['league_id']}' AND home_hc.hcd_handicap=home_player.handicap_mixed_9)";
		$vc = "(visit_hc.hcd_league='{$row['league_id']}' AND visit_hc.hcd_handicap=visit_player.handicap_mixed_9)";
	break;

	case '5':
		$hc = "(home_hc.hcd_league='{$row['league_id']}' AND home_hc.hcd_handicap=home_player.handicap_10)";
		$vc = "(visit_hc.hcd_league='{$row['league_id']}' AND visit_hc.hcd_handicap=visit_player.handicap_10)";
	break;
		
	case '6':
		$hc = "(home_hc.hcd_league='{$row['league_id']}' AND home_hc.hcd_handicap=home_player.handicap_eight_beginner)";
		$vc = "(visit_hc.hcd_league='{$row['league_id']}' AND visit_hc.hcd_handicap=visit_player.handicap_eight_beginner)";
	break;	
}


$result = mysql_query("SELECT 
						match_schedule.match_id, match_schedule.match_number, match_schedule.match_start_date,
						DATE_FORMAT(match_schedule.match_start_date,'%M %D %Y') match_date,
						home_team.name home_team, CONCAT(home_player.first_name,' ',home_player.last_name) home_player, home_team.team_id home_team_id,
						IF(hrt.is_win=1,'WIN','LOSS') home_win, hrt.is_win hrt_is_win,
						IF(home_result.is_win=1,'WIN','LOSS') home_score, home_result.is_win home_point,
						visit_team.name visit_team, CONCAT(visit_player.first_name,' ',visit_player.last_name) visit_player, visit_team.team_id visit_team_id,
						IF(vrt.is_win=1,'WIN','LOSS') visit_win, vrt.is_win vrt_is_win,
						IF(visit_result.is_win=1,'WIN','LOSS') visit_score, visit_result.is_win visit_point,
						home_result.games_won home_points, visit_result.games_won visit_points
						FROM match_schedule 
						RIGHT JOIN team home_team ON home_team.team_id=match_schedule.home_team_id
						RIGHT JOIN team visit_team ON visit_team.team_id=match_schedule.visit_team_id
						RIGHT JOIN result_team hrt ON (hrt.match_id=match_schedule.match_id AND hrt.team_id=home_team.team_id)
						RIGHT JOIN result_team vrt ON (vrt.match_id=match_schedule.match_id AND vrt.team_id=visit_team.team_id)
						RIGHT JOIN result_ind home_result ON 
						(home_result.match_id=match_schedule.match_id AND home_result.team_id=home_team.team_id)
						RIGHT JOIN result_ind visit_result ON 
						(visit_result.match_id=match_schedule.match_id AND visit_result.team_id=visit_team.team_id 
						AND visit_result.match_number=home_result.match_number)
						
						RIGHT JOIN player home_player ON home_player.player_id=home_result.player_id
						RIGHT JOIN player visit_player ON visit_player.player_id=visit_result.player_id
						
						LEFT JOIN handicap_display home_hc ON {$hc}
						LEFT JOIN handicap_display visit_hc ON {$vc}
						
						WHERE division_id='{$_GET['division_id']}' AND 
						(match_schedule.home_team_id='{$_GET['team_id']}' OR match_schedule.visit_team_id='{$_GET['team_id']}') 
						AND NOT home_player.player_id IN (218,224)
						ORDER BY match_schedule.match_number");

$match_date = '';

echo mysql_error();

while($row = mysql_fetch_assoc($result))
{
	if ( (!$last_match_date == '') && ($last_match_date != $row['match_start_date']) )
	{
		$view->parse('main.night');
	
		if ($row['home_team_id'] == $_GET['team_id'])
		{
			$win_total += $row['hrt_is_win'];
			$loss_total += $row['vrt_is_win'];
		}
		else
		{
			$win_total += $row['vrt_is_win'];
			$loss_total += $row['hrt_is_win'];
		}
		
		$home_point_total  = 0;
		$visit_point_total = 0;
	}
	
	foreach($row as $key => $val)
		$view->assign($key, $val);
	
	if ($row['home_team_id'] == $_GET['team_id'])
		$point_total += $row['home_point'];
	else
		$point_total += $row['visit_point'];
	
	$home_point_total += $row['home_point'];
	$visit_point_total += $row['visit_point'];
	
	$last_match_date = $row['match_start_date'];
	
	$view->assign('home_point_total', $home_point_total);
	$view->assign('visit_point_total', $visit_point_total);
	
	$view->parse('main.night.match');
}

$view->assign('win_total', $win_total);
$view->assign('loss_total', $loss_total);
$view->assign('point_total', $point_total);


$view->parse('main');
$view->out('main');

?>