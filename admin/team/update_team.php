<?php

include '../../include/mysql.php';
	
$set .= 
"name='"      . addslashes(urldecode($_GET['name']))      . "', " . 
"league_id='" . addslashes(urldecode($_GET['league_id'])) . "', " . 
"status='"    . addslashes(urldecode($_GET['status']))    . "'";

mysql_query("UPDATE team SET {$set} WHERE team_id='{$_GET['team_id']}'");

if (mysql_affected_rows() < 1)
	echo mysql_error() . $set;
else
	echo '1';
?>