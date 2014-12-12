<?php

include '../../include/mysql.php';
include '../../include/functions.php';

$result = mysql_query("SELECT * FROM division WHERE division_id='{$_GET['division_id']}'");
$row = mysql_fetch_assoc($result);

mysql_query("DELETE FROM division_member WHERE division_id='{$_GET['division_id']}'");

if (strlen($_GET['individuals']) > 0)
{					
	$individual = explode('.', urldecode($_GET['individuals']));
						
	foreach($individual as $player_id)
	{
		mysql_query("INSERT INTO division_member SET 
						league_id='{$row['league_id']}', 
						season_id='{$row['season_id']}',
						division_id='{$row['division_id']}', 
						player_id='{$player_id}'");	
	}
}

echo mysql_affected_rows();
?>