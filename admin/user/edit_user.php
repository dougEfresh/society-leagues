<?php

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('edit_user.html');

$result = mysql_query("SELECT * FROM player WHERE player_id='{$_GET['player_id']}'");

$row = mysql_fetch_assoc($result);

foreach ($row as $key => $val)
	$view->assign($key, $val);
	
$result = mysql_query("SELECT * FROM groups");

while($group = mysql_fetch_assoc($result))
{
	foreach($group as $key => $val)
		$view->assign($key, $val);
		
	if ($row['player_group'] == $group['g_id'])
		$view->assign('checked', 'checked');
	else
		$view->assign('checked', '');	
		
	$view->parse('main.group');	
}

$result = mysql_query("SELECT * FROM league LEFT JOIN handicap_display ON hcd_league=league_id ORDER BY hcd_handicap");

while($row = mysql_fetch_assoc($result))
{
	foreach($row as $key => $val)
		$view->assign($key, $val);
		
	$view->parse("main.hc.{$row['league_id']}");		
}

$view->parse("main.hc");	

$result = mysql_query("SELECT
						team.name, team.team_id,
						division.division_id,
						CONCAT(league.league_type,' ',league.league_game,' - ',days.d_name) league_name,
						season.season_number, season.season_year,
						COUNT(result_ind.is_win) match_count,
						SUM(result_ind.is_win) match_wins,
						(COUNT(result_ind.is_win)-SUM(result_ind.is_win)) match_losses,
						(ROUND(SUM(result_ind.is_win) / COUNT(result_ind.is_win),3)) percentage
						FROM result_ind
						RIGHT JOIN match_schedule ON match_schedule.match_id=result_ind.match_id
						RIGHT JOIN division ON division.division_id=match_schedule.division_id
						RIGHT JOIN season ON season.season_id=division.season_id
						RIGHT JOIN days ON days.d_id=division.division_day
						RIGHT JOIN league ON league.league_id=division.league_id
						RIGHT JOIN player ON player.player_id=result_ind.player_id
						LEFT JOIN team ON team.team_id=result_ind.team_id
						WHERE result_ind.player_id='{$_GET['player_id']}' GROUP BY division.division_id;");

while($row = mysql_fetch_assoc($result))
{
	foreach($row as $key => $val)
		$view->assign($key, $val);
	
	$view->parse('main.user_history');
}

			
$view->parse('main');
$view->out('main');

?>