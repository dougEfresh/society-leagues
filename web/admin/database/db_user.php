<?php

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('db_user.html');

$result = mysql_query("
SELECT
CONCAT(player.first_name,' ',player.last_name) player_name, player.email, player.player_id,
(SELECT COUNT(*) FROM result_ind WHERE result_ind.player_id=player.player_id) game_count,
(SELECT COUNT(*) FROM division_dues WHERE division_dues.d_player=player.player_id) dues_count,
(SELECT COUNT(*) FROM team_player WHERE team_player.tp_player=player.player_id) team_count
FROM player
LIMIT 10");


while($row = mysql_fetch_assoc($result))
{
	foreach($row as $key => $val)
		$view->assign($key, $val);
		
	$view->parse('main.user');
}
			
$view->parse('main');
$view->out('main');

?>