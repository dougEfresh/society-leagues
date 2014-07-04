<?php
include '../../include/mysql.php';

$set = 
"name='"       . addslashes(urldecode($_GET['name'])) . "'," .
"league_id='"  . addslashes(urldecode($_GET['league_id'])) . "'"; 

mysql_query("INSERT INTO team SET {$set}");

if (mysql_affected_rows() > 0)
	echo mysql_insert_id();
else
	echo mysql_error();
?>