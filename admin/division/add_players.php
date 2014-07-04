<?php

include '../../include/mysql.php';
include '../../include/functions.php';

$player = explode('.', urldecode($_GET['players']));

$result = mysql_query("SELECT * FROM team_player WHERE tp_team='{$_GET['team_id']}' AND tp_division='{$_GET['division_id']}'");

while ($row = mysql_fetch_assoc($result))
{
	mysql_query("DELETE FROM team_player WHERE tp_id='{$row['tp_id']}'");
	mysql_query("DELETE FROM division_dues WHERE d_division='{$row['tp_division']}' AND d_player='{$row['tp_player']}' AND d_team='{$row['tp_team']}'");
}
						
foreach($player as $player_id)
{
	mysql_query("INSERT INTO team_player SET tp_team='{$_GET['team_id']}', tp_player='{$player_id}', tp_division='{$_GET['division_id']}'");
	mysql_query("INSERT INTO division_dues SET d_division='{$_GET['division_id']}', 
					d_player='{$player_id}', d_team='{$_GET['team_id']}', d_amount='35'");
}
echo mysql_affected_rows();
?>