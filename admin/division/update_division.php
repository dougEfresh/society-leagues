<?php

include '../../include/mysql.php';
include '../../include/functions.php';

$start_date = jquery_to_mysql_date(urldecode($_GET['start_date']));
$end_date   = jquery_to_mysql_date(urldecode($_GET['end_date']));

$set = 
"start_date='"	. addslashes($start_date) . "', " . 
"end_date='"	. addslashes($end_date) . "'";

mysql_query("UPDATE division SET {$set} WHERE division_id='{$_GET['division_id']}'");

if (mysql_affected_rows() > 0)
{
	mysql_query("DELETE FROM match_schedule WHERE division_id='{$_GET['division_id']}'");
	echo '1';
}
else
	die("There was an error updating this division.\r\n\r\n" . mysql_error());

?>