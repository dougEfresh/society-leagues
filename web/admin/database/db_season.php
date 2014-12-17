<?php

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('db_season.html');

$result = mysql_query("
SELECT
division.division_id,

league.league_id,
season.season_id,
division_member.division_member_id,

team.team_id, team.name

FROM division

LEFT JOIN season ON season.season_id=division.season_id
LEFT JOIN league ON league.league_id=division.league_id

LEFT JOIN division_member ON division_member.division_id=division.division_id
LEFT JOIN team ON team.team_id=division_member.team_id

WHERE division.division_id='1'");


while($row = mysql_fetch_assoc($result))
{
	foreach($row as $key => $val)
		$view->assign($key, $val);
		
	$view->parse('main.division.team');
}

$view->parse('main.division');			
$view->parse('main');
$view->out('main');

?>