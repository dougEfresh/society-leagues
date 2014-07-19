<?php

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('bca_stats.html');

$result = mysql_query("SELECT player.*, league.*,
result_team.match_id, result_team.team_id, result_ind.player_id, team.name team_name,
CONCAT(player.first_name,' ',player.last_name) player_name,
COUNT(result_ind.result_id) matches,
SUM(result_ind.is_win) wins,
SUM(IF(result_ind.is_win,0,1)) losses
FROM division 
JOIN match_schedule ON match_schedule.division_id=division.division_id
JOIN result_team ON result_team.match_id=match_schedule.match_id
JOIN result_ind ON result_ind.match_id=match_schedule.match_id
JOIN team ON team.team_id=result_team.team_id
JOIN player ON player.player_id=result_ind.player_id
JOIN league ON league.league_id=division.league_id
WHERE division.division_id='{$_GET['division_id']}'
GROUP BY result_ind.player_id
ORDER BY team.team_id");

$text = '';

$filename = 'division_' . $_GET['division_id'] . '.csv';

$fullfile = '../../temp/' . $filename;
unlink($fullfile);

$text = 
"TEAM," . 
"FIRST NAME," . 
"LAST NAME," . 
"MATCHES," . 
"RECORD," . 
"EMAIL," . 
"PHONE," . 
"ADDRESS\n";

file_put_contents($fullfile, $text);
	
while($row = mysql_fetch_assoc($result))
{
	foreach($row as $key => $val)
		$view->assign($key, $val);
	
	$text = file_get_contents($fullfile);
	
	$text .= 
	$row['team_name'] . "," . 
	$row['first_name'] . "," . 
	$row['last_name'] . "," . 
	$row['matches'] . "," . 
	$row['wins'] . " -- " . $row['losses'] . "," . 
	$row['email'] . "," . 
	$row['phone'] . "," . 
	$row['street1'] . "," . 
	$row['street2'] . "," . 
	$row['city'] . "," . 
	$row['state'] . "," . 
	$row['zip'] . "\n";
	
	file_put_contents($fullfile, $text);
		
	$view->parse('main.player');
}

if (file_exists($fullfile))
{
	$view->assign("filename", 'http://'.$_SERVER['HTTP_HOST'].'/temp/'.$filename);
	$view->parse('main.links');
}

$view->parse('main');
$view->out('main');

?>