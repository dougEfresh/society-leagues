<?php

include '../../include/mysql.php';
include '../../include/functions.php';

$playoff_id = $_GET['playoff_id'];
$division_id = $_GET['division_id'];

$matches = mysql_query("SELECT * FROM match_schedule WHERE division_id=$division_id ORDER BY match_number DESC LIMIT 1");
$row = mysql_fetch_assoc($matches);

$next_match = $row['match_number'] + 1;

$playoff_types = mysql_query("SELECT * FROM playoff_types WHERE playoff_type_id=$playoff_id");
$playoff_row = mysql_fetch_assoc($playoff_types);

$games = explode(",", $playoff_row['playoff_type_teams']);

$timegap = 7;
foreach ($games as &$teams) {
    $matches = $teams / 2;
	//error_log("this is a test: $teams: $matches",0);

	$i = 1;
	while ($i <= $matches) {
		//error_log("Adding matches: $matches: $next_match: $i : $timegap",0);

		mysql_query("INSERT INTO match_schedule SET 
				division_id=$division_id, 
				match_number='{$next_match}', 
				match_start_date=DATE_ADD('{$row['match_start_date']}', INTERVAL $timegap DAY),
				season_id='{$row['season_id']}',
				league_id='{$row['league_id']}', playoffs='1'");
				
		$i++;  

	}
	$next_match++;
	$timegap = $timegap + 7;
}

echo mysql_affected_rows();
?>