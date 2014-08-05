<?php

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('edit_division.html');

$result = mysql_query(
"SELECT *, DATE_FORMAT(division.start_date,'%m/%d/%Y') jq_start, DATE_FORMAT(division.end_date,'%m/%d/%Y') jq_end 
FROM division
RIGHT JOIN league ON league.league_id=division.league_id
RIGHT JOIN season ON season.season_id=division.season_id
RIGHT JOIN days ON  d_id=division.division_day
RIGHT JOIN season_name ON season_name.sn_id=season.season_number
WHERE division.division_id='{$_GET['division_id']}'");

$row = mysql_fetch_assoc($result);

foreach ($row as $key => $val)
	$view->assign($key, $val);

$result = mysql_query(
"SELECT
division.division_id, league.league_type,
team.team_id team_id, team.name team_name,
COUNT(result_team.result_id) team_games,
SUM(result_team.is_win) team_wins,
(COUNT(result_team.result_id) - SUM(result_team.is_win)) team_losses,
(ROUND(SUM(result_team.is_win) / COUNT(result_team.result_id),3)) AS team_percentage,
player.player_id player_id, CONCAT(player.first_name,' ',player.last_name) player_name,
COUNT(result_ind.result_id) individual_games,
SUM(result_ind.is_win) individual_wins,
(COUNT(result_ind.result_id) - SUM(result_ind.is_win)) individual_losses,
(ROUND(SUM(result_ind.is_win) / COUNT(result_ind.result_id),3)) AS individual_percentage
FROM division_member 
LEFT JOIN division ON division.division_id=division_member.division_id
LEFT JOIN match_schedule ON match_schedule.division_id=division.division_id
LEFT JOIN team ON team.team_id=division_member.team_id
LEFT JOIN result_team ON (result_team.team_id=team.team_id AND result_team.match_id=match_schedule.match_id)
LEFT JOIN player ON player.player_id=division_member.player_id
LEFT JOIN result_ind ON (result_ind.player_id=player.player_id AND result_ind.match_id=match_schedule.match_id)
RIGHT JOIN league ON league.league_id=division.league_id
WHERE division_member.division_id='{$_GET['division_id']}'
GROUP BY team.team_id, player.player_id
ORDER BY team_wins DESC,team_percentage DESC,individual_wins DESC,individual_percentage DESC");

if (mysql_num_rows($result) > 0)
{
	while ($row = mysql_fetch_assoc($result))
	{
		foreach($row as $key => $val)
			$view->assign($key, $val);
		
		$view->parse("main.{$row['league_type']}");
		
		$playoffs = $row['playoffs'];
	}
}
else
{
	$view->parse("main.empty");
	$view->parse("main.delete_button");
}

$result = mysql_query(
"SELECT * FROM division 
JOIN match_schedule ON match_schedule.division_id=division.division_id
JOIN result_team ON result_team.match_id=match_schedule.match_id
WHERE division.division_id='{$_GET['division_id']}'");

if (mysql_num_rows($result) == 0)
	$view->parse('main.update_button');
	
$res = mysql_query("SELECT * FROM match_schedule WHERE division_id='{$_GET['division_id']}' AND playoffs='1'");
	
if (mysql_num_rows($res) == 0)
	$view->parse('main.no_playoffs');
if (mysql_num_rows($res) > 0) 
	$view->parse('main.delete_playoffs');

$view->parse('main');
$view->out('main');

?>