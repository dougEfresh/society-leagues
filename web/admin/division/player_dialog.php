<?php

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('player_dialog.html');

$result = mysql_query("
SELECT
CONCAT(player.first_name,' ',player.last_name) player_name, player.player_id, team.team_id, team.name,
IF(current.tp_player=player.player_id,'current',IF(former.tp_player=player.player_id,'former','new')) player_status
FROM player JOIN team ON team_id='{$_GET['team_id']}'
LEFT JOIN team_player current ON current.tp_team=team.team_id AND current.tp_player=player.player_id AND current.tp_division='{$_GET['division_id']}'
LEFT JOIN team_player former ON former.tp_team=team.team_id AND former.tp_player=player.player_id AND NOT former.tp_division='{$_GET['division_id']}'
GROUP BY player.player_id ORDER BY player.last_name, player.first_name");

$view->assign('division_id', $_GET['division_id']);

while($row = mysql_fetch_assoc($result))
{
	foreach($row as $key => $val)
		$view->assign($key, $val);
	
	$view->parse("main.{$row['player_status']}");
}

$view->parse('main');
$view->out('main');

?>