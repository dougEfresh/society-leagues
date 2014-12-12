<?php

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('team_dialog.html');

$result = mysql_query("SELECT * FROM division 
JOIN league ON league.league_id=division.league_id 
WHERE division.division_id='{$_GET['division_id']}'");

$row = mysql_fetch_assoc($result);

$league_type = $row['league_type'];

if ($league_type == 'Team')
{
	$result = mysql_query("
	SELECT 
	division.division_id, team.team_id, team.name, team.status,
	IF(division_member.division_id IS NULL,'not_assigned','assigned') assigned
	FROM division 
	JOIN team ON team.league_id=division.league_id
	LEFT JOIN division_member ON division_member.division_id=division.division_id AND division_member.team_id=team.team_id
	WHERE division.division_id='{$_GET['division_id']}' ORDER BY team.name");
}
else
{
	$result = mysql_query("
	SELECT 
	division.division_id, player.player_id, CONCAT(player.first_name,' ',player.last_name) name,
	IF(division_member.division_id IS NULL,'not_assigned','assigned') assigned
	FROM division 
	LEFT JOIN player ON NOT player.player_id IS NULL
	LEFT JOIN division_member ON division_member.division_id=division.division_id AND division_member.player_id=player.player_id
	WHERE division.division_id='{$_GET['division_id']}'");	
}

echo mysql_error();
while($row = mysql_fetch_assoc($result))
{
	foreach($row as $key => $val)
		$view->assign($key, $val);
	
	$view->parse("main.{$league_type}.{$row['assigned']}");
}

$view->parse("main.{$league_type}");

$view->parse('main');
$view->out('main');

?>