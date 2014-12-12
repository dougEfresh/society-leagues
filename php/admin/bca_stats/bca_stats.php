<?php

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('bca_stats.html');

$resultleagues = mysql_query("SELECT * FROM division 
						JOIN league ON league.league_id=division.league_id
						WHERE division_id='{$_GET['division_id']}'");

$row = mysql_fetch_assoc($resultleagues);
$leagueID = $row['league_id'];
$scramble = 0;

switch($leagueID)
{
	case '1':
		$hc = "hcd_id=player.hc_9";
	break;
	
	case '2':
		$hc = "hcd_id=player.hc_8";
	break;
	
	case '3':
		$hc = "hcd_id=player.hc_straight";
	break;
	
	case '4':
		$hc = "hcd_id=player.hc_m9";
		$hc8 = "hcm8.hcd_id=player.hc_m8";
		$scramble = 1; 
	break;

	case '5':
		$hc = "hcd_id=player.hc_10";
	break;
		
	case '6':
		$hc = "hcd_id=player.hc_8Begin";
	break;	
}

$result = mysql_query("SELECT CONCAT(player.first_name,' ',player.last_name) player_name, player.*, handicap_display.*,
team.name team_name,
COUNT(result_ind.result_id) matches,
SUM(result_ind.is_win) wins, (COUNT(result_ind.result_id) - SUM(result_ind.is_win)) losses,
(ROUND(SUM(result_ind.is_win) / COUNT(result_ind.result_id),3)) AS percentage
FROM team_player
RIGHT JOIN player ON player.player_id=team_player.tp_player
LEFT JOIN handicap_display ON {$hc} 
RIGHT JOIN team ON team.team_id=team_player.tp_team
LEFT JOIN division ON division.division_id='{$_GET['division_id']}'
LEFT JOIN match_schedule ON (match_schedule.division_id=division.division_id AND 
(match_schedule.home_team_id=team_player.tp_team OR match_schedule.visit_team_id=team_player.tp_team))
LEFT JOIN result_ind ON result_ind.match_id=match_schedule.match_id AND result_ind.player_id=player.player_id AND result_ind.team_id=team.team_id
WHERE team_player.tp_division='{$_GET['division_id']}'
AND NOT player.first_name LIKE '%FORFEIT%' AND NOT player.first_name LIKE '%HANDICAP%'
AND NOT player.last_name LIKE '%FORFEIT%' AND NOT player.last_name LIKE '%HANDICAP%'
GROUP BY tp_player ORDER BY name ASC, player_name ASC");

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