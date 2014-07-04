<?php
session_start();

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('teams.html');

if ( (isset($_SESSION['team_filter'])) && (strlen($_SESSION['team_filter']) > 0))
	$where = "WHERE name LIKE '%{$_SESSION['team_filter']}%'";
else
	$where = '';
	
$result = mysql_query("SELECT * FROM team 
						JOIN league ON league.league_id=team.league_id
						{$where} AND NOT team_id IN (36,40,77,115,116) ORDER BY name");

while ($row = mysql_fetch_assoc($result))
{
	foreach ($row as $key => $val)
		$view->assign($key, $val);
		
	$view->parse('main.team');		
}

$view->parse('main');
$view->out('main');

?>