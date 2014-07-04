<?php

include '../../include/mysql.php';
include '../../include/functions.php';

$division_id  = $_GET['division_id'];
$match_number = $_GET['match_number'];
$new_matchup  = explode('.', urldecode($_GET['new_matchups']));

foreach($new_matchup as $nm)
{
	$n = explode(':', $nm);
	
	mysql_query("UPDATE match_schedule SET home_team_id='{$n['1']}', visit_team_id='{$n['2']}' 
	WHERE match_id='{$n['0']}' AND division_id='{$division_id}' AND match_number='{$match_number}'");
}

echo mysql_affected_rows();
?>