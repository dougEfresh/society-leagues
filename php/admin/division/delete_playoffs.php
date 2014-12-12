<?php

include '../../include/mysql.php';
include '../../include/functions.php';

$result = mysql_query("SELECT * FROM match_schedule WHERE division_id='{$_GET['division_id']}' AND playoffs='1'");

if (mysql_num_rows($result) > 0)
{
	$row = mysql_fetch_assoc($result);
	
	mysql_query("DELETE FROM match_schedule WHERE division_id='{$_GET['division_id']}' AND playoffs='1'");
	$sc = mysql_affected_rows();
	
	die(($dc) . '');
}

die('0');
?>