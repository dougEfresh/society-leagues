<?php

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('edit_team.html');

$result = mysql_query("SELECT * FROM team WHERE team_id='{$_GET['team_id']}'");

$row = mysql_fetch_assoc($result);

foreach ($row as $key => $val)
	$view->assign($key, $val);

$league_id = $row['league_id'];

$result = mysql_query("SELECT * FROM league");

while($row = mysql_fetch_assoc($result))
{
	foreach($row as $key => $val)
		$view->assign($key, $val);
	
	$view->parse('main.league');
}

$result = mysql_query("SELECT * FROM team_division WHERE team_id='{$_GET['team_id']}' ORDER BY season_year DESC");

while($row = mysql_fetch_assoc($result))
{
	foreach($row as $key => $val)
		$view->assign($key, $val);
	
	$view->parse('main.division');
}

$view->assign('league_id', $league_id);

$view->parse('main');
$view->out('main');

?>