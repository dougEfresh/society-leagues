<?php
session_start();

include '../include/mysql.php';
include '../include/xtemplate.class.php';

$view = new xtemplate('navigation.html');

$result = mysql_query("SELECT * FROM player 
						LEFT JOIN navigation ON (n_group=player_group OR n_group='0')
						WHERE player_id='{$_SESSION['player_id']}' ORDER BY n_order");

echo mysql_error();

while($row = mysql_fetch_assoc($result))
{
	foreach ($row as $key => $val)
		$view->assign($key, $val);
		
	$view->parse('main.item');	
}

$view->parse('main');
$view->out('main');

?>