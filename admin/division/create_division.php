<?php

include '../../include/mysql.php';
include '../../include/functions.php';

$start_date   = jquery_to_mysql_date(urldecode($_GET['start_date']));
$end_date     = jquery_to_mysql_date(urldecode($_GET['end_date']));
$league_id    = urldecode($_GET['league_id']);
$time_slot    = urldecode($_GET['time_slot']);
$division_day = date('w', strtotime($start_date)) + 1;
$season_year  = date('Y', strtotime($start_date));

$sn[1] = $sn[2] = $sn[12]  = 1;
$sn[3] = $sn[4] = $sn[5]   = 2;
$sn[6] = $sn[7] = $sn[8]   = 3;
$sn[9] = $sn[10] = $sn[11] = 4;

$set = 
"season_year='"	  . addslashes($season_year) . "', " . 
"season_number='" . addslashes($sn[(int)date('m', strtotime($start_date))]) . "', " . 
"league_id='"     . addslashes($league_id) ."'";

mysql_query("INSERT INTO season SET {$set}");

if (mysql_affected_rows() > 0)
{
	$set = 
	"start_date='"	 . addslashes($start_date) . "', " . 
	"end_date='"     . addslashes($end_date) . "', " . 
	"division_day='" . addslashes($division_day) . "', " . 
	"time_slot='"    . addslashes($time_slot) . "', " . 
	"season_id='"    . addslashes(mysql_insert_id()) . "', " . 
	"league_id='"    . addslashes($league_id) ."'";

	mysql_query("INSERT INTO division SET {$set}");
	
	if (mysql_affected_rows() > 0)
		die(mysql_insert_id().'');
	else
		die(mysql_error());
}
else
	die(mysql_error());
?>