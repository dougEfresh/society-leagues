<?php

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('captain_dialog.html');

$view->assign('division_id', $_GET['division_id']);
$view->assign('team_id', $_GET['team_id']);

$result = mysql_query("
SELECT  CONCAT(player.first_name,' ',player.last_name) player_name, player_id
FROM player 
JOIN team_player ON team_player.tp_player=player.player_id
WHERE team_player.tp_team='{$_GET['team_id']}'AND team_player.tp_division = '{$_GET['division_id']}'
ORDER BY player.last_name, player.first_name
");

echo mysql_error();



while ($row = mysql_fetch_assoc($result))
	{

		foreach($row as $key => $val)
			$view->assign($key, $val);
		$view->parse("main.captain_id");
}

$view->parse('main');
$view->out('main');

?>