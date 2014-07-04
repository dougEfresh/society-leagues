<?php
session_start();

include '../include/mysql.php';
include '../include/xtemplate.class.php';

$view = new xtemplate('home_load_team.html');

$result = mysql_query("
SELECT *, CONCAT(season_name.sn_name,' ',season.season_year) season_name
FROM division_member 
JOIN team ON team.team_id=division_member.team_id
JOIN division ON division.division_id=division_member.division_id
JOIN season ON season.season_id=division.season_id
JOIN league ON league.league_id=division.league_id
JOIN season_name ON season_name.sn_id=season.season_number
WHERE division_member.team_id='{$_GET['team_id']}' ORDER BY division.start_date DESC");

echo mysql_error();
while ($row = mysql_fetch_assoc($result))
{
	foreach ($row as $key => $val)
		$view->assign($key, $val);
		
	$view->parse('main.division');
}

$view->parse('main');
$view->out('main');

?>