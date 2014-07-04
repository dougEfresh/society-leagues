<?php

include '../../include/mysql.php';
include '../../include/functions.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('generate_schedule.html');

mysql_query("DELETE FROM match_schedule WHERE division_id='{$_GET['division_id']}'");

$result = mysql_query("
SELECT * FROM division 
JOIN league ON league.league_id=division.league_id 
WHERE division_id='{$_GET['division_id']}'");

$division_info = mysql_fetch_assoc($result);

if ($division_info['league_type'] == 'Team')
	$result = mysql_query(
	"SELECT *, team.team_id member_id FROM division_member 
	RIGHT JOIN team ON team.team_id=division_member.team_id
	RIGHT JOIN division ON division.division_id=division_member.division_id
	WHERE division_member.division_id='{$_GET['division_id']}'");
else
	$result = mysql_query(
	"SELECT *, player.player_id member_id FROM division_member 
	RIGHT JOIN player ON player.player_id=division_member.player_id
	RIGHT JOIN division ON division.division_id=division_member.division_id
	WHERE division_member.division_id='{$_GET['division_id']}'");

$common_error = "\n\nThe season will be auto generated once you add teams to the division.";
$common_error .= "\n\nIf you want to delete this division, remove all of the division teams/individuals.";

if (mysql_num_rows($result) < 1)
	die("ERROR*There are no teams available to create a schedule\nClick on the 'Manage Divsion Teams' button to add teams.".$common_error);
	
if ((mysql_num_rows($result) & 1) == 1)
	die("ERROR*You have an odd numbers of teams in this division.\nYou must add the 'Bye Week' team.".$common_error);
	
while($row = mysql_fetch_assoc($result))
	$member_list[] = $row['member_id'];

$diff = abs(strtotime($division_info['end_date']) - strtotime($division_info['start_date']));
$years = floor($diff / (365*60*60*24));
$weeks = floor((($diff - $years * 365*60*60*24) / (60*60*24)) / 7) + 1;

$schedule = round_robin($member_list, $weeks);

$offset = 0;
$current_week = 1;

foreach($schedule as $week => $matchups)
{	
	foreach($matchups as $m)
	{
		if ($division_info['league_type'] == 'Team')
			mysql_query("
			INSERT INTO match_schedule SET 
			league_id='{$division_info['league_id']}',
			season_id='{$division_info['season_id']}',
			division_id='{$division_info['division_id']}',
			home_team_id='{$m[0]}', visit_team_id='{$m[1]}',
			match_number='{$current_week}',
			match_start_date=DATE_ADD('{$division_info['start_date']}',INTERVAL {$offset} DAY)");
		else
			mysql_query("
			INSERT INTO match_schedule SET 
			league_id='{$division_info['league_id']}',
			season_id='{$division_info['season_id']}',
			division_id='{$division_info['division_id']}',
			home_player_id='{$m[0]}', visit_player_id='{$m[1]}',
			match_number='{$current_week}',
			match_start_date=DATE_ADD('{$division_info['start_date']}',INTERVAL {$offset} DAY)");
		
		$home[$m[0]]++;
		
		$view->assign('home', $m[0]);
		$view->assign('away', $m[1]);
		$view->parse('main.week.match');
	}
	
	$offset = $offset + 7;
	$current_week++;
	
	$view->assign('week', $week);
	$view->parse('main.week');
}

foreach($home as $team => $count)
{
	$view->assign('team', $team);
	$view->assign('count', $count);
	$view->parse('main.home');
}

$view->parse('main');
$view->out('main');
?>