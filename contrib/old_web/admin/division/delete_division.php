<?php

include '../../include/mysql.php';
include '../../include/functions.php';

$result = mysql_query("SELECT * FROM division WHERE division_id='{$_GET['division_id']}'");

if (mysql_num_rows($result) > 0)
{
	$row = mysql_fetch_assoc($result);
	
	mysql_query("DELETE FROM season WHERE season_id='{$row['season_id']}'");
	$sc = mysql_affected_rows();
	
	mysql_query("DELETE FROM division WHERE division_id='{$row['division_id']}'");
	$dc = mysql_affected_rows();
	
	die(($dc + $sc) . '');
}

die('0');
?>