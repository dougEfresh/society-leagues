<?php

include '../../include/mysql.php';
include '../../include/functions.php';

$result = mysql_query("SELECT * FROM match_schedule WHERE division_id='{$_GET['division_id']}' ORDER BY match_number DESC LIMIT 1");
$row = mysql_fetch_assoc($result);

$first = $row['match_number'] + 1;
$last  = $row['match_number'] + 2;

mysql_query("INSERT INTO match_schedule SET 
				division_id='{$_GET['division_id']}', 
				match_number='{$first}', 
				match_start_date=DATE_ADD('{$row['match_start_date']}', INTERVAL 7 DAY),
				season_id='{$row['season_id']}',
				league_id='{$row['league_id']}', playoffs='1'");

mysql_query("INSERT INTO match_schedule SET 
				division_id='{$_GET['division_id']}', 
				match_number='{$first}', 
				match_start_date=DATE_ADD('{$row['match_start_date']}', INTERVAL 7 DAY),
				season_id='{$row['season_id']}',
				league_id='{$row['league_id']}', playoffs='1'");

mysql_query("INSERT INTO match_schedule SET 
				division_id='{$_GET['division_id']}', 
				match_number='{$last}', 
				match_start_date=DATE_ADD('{$row['match_start_date']}', INTERVAL 14 DAY),
				season_id='{$row['season_id']}',
				league_id='{$row['league_id']}', playoffs='1'");

echo mysql_affected_rows();
?>