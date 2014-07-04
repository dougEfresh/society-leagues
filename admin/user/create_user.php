<?php
include '../../include/mysql.php';

$set = 
"first_name='"   . addslashes(urldecode($_GET['first_name'])) . "'," .
"last_name='"    . addslashes(urldecode($_GET['last_name'])) . "'," . 
"player_login='" . addslashes(urldecode($_GET['player_login'])) . "'," . 
"email='"        . addslashes(urldecode($_GET['email'])) . "'," .
"password='"     . addslashes(urldecode($_GET['password'])) . "'";

mysql_query("INSERT INTO player SET {$set}");

if (mysql_affected_rows() > 0)
	echo mysql_insert_id();
else
	echo mysql_error();
?>