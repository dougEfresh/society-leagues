<?php

include '../include/mysql.php';
include '../include/xtemplate.class.php';

$view = new xtemplate('team_roster.html');

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

if ($scramble == 1	 ){
$result = mysql_query("
SELECT CONCAT(player.first_name,' ',player.last_name) player_name, player.player_id, handicap_display.*, hcm8.hcd_name as hcd_name8,
team.name,
COUNT(result_ind.result_id) games,
(ROUND(SUM(result_ind.is_win) / COUNT(result_ind.result_id),3)) AS percentage,
SUM(IF (match_schedule.scramble9 = 1 AND result_ind.is_win AND result_ind.scotch = 0, 1, 0)) AS wins9ball,

SUM(IF (match_schedule.scramble9 = 1 AND NOT result_ind.is_win AND result_ind.scotch = 0, 1, 0)) AS loss9ball,

ROUND((SUM(IF (match_schedule.scramble9 = 1 AND result_ind.is_win AND result_ind.scotch = 0, 1, 0))) / SUM(IF (match_schedule.scramble9 = 1 AND result_ind.scotch = 0, 1, 0)) ,2)  AS pct9ball,

SUM(IF (match_schedule.scramble9 = 0 AND result_ind.is_win AND result_ind.scotch = 0, 1, 0)) AS wins8ball,

SUM(IF (match_schedule.scramble9 = 0 AND NOT result_ind.is_win AND result_ind.scotch = 0, 1, 0)) AS loss8ball,

ROUND((SUM(IF (match_schedule.scramble9 = 0 AND result_ind.is_win AND result_ind.scotch = 0, 1, 0))) / SUM(IF (match_schedule.scramble9 = 0 AND result_ind.scotch = 0, 1, 0)) ,2)  AS pct8ball,

SUM(IF (result_ind.is_win AND result_ind.scotch = 1, 1, 0)) AS winsscotch,

SUM(IF (NOT result_ind.is_win AND result_ind.scotch = 1, 1, 0)) AS lossscotch,

ROUND((SUM(IF (result_ind.is_win AND result_ind.scotch = 1, 1, 0))) / SUM(IF (result_ind.scotch = 1, 1, 0)),2 ) AS pctscotch,
SUM(result_ind.is_win) wins,
(COUNT(result_ind.result_id) - SUM(result_ind.is_win)) losses

FROM team_player
RIGHT JOIN player ON player.player_id=team_player.tp_player
LEFT JOIN handicap_display ON {$hc} 
LEFT JOIN handicap_display AS hcm8 ON {$hc8}
RIGHT JOIN team ON team.team_id=team_player.tp_team
LEFT JOIN division ON division.division_id='{$_GET['division_id']}'
LEFT JOIN match_schedule ON (match_schedule.division_id=division.division_id AND 
(match_schedule.home_team_id=team_player.tp_team OR match_schedule.visit_team_id=team_player.tp_team))
LEFT JOIN result_ind ON result_ind.match_id=match_schedule.match_id AND result_ind.player_id=player.player_id AND result_ind.team_id=team.team_id
WHERE team_player.tp_team='{$_GET['team_id']}' AND team_player.tp_division='{$_GET['division_id']}'
AND NOT player.first_name LIKE '%FORFEIT%' AND NOT player.first_name LIKE '%HANDICAP%'
AND NOT player.last_name LIKE '%FORFEIT%' AND NOT player.last_name LIKE '%HANDICAP%'
GROUP BY tp_player ORDER BY percentage DESC
");

}
else {
$result = mysql_query("
SELECT CONCAT(player.first_name,' ',player.last_name) player_name, player.player_id, handicap_display.*,
team.name,
CONCAT('$',FORMAT(IF(dp_id IS NULL,d_amount,d_amount - SUM(dp_payment)),2)) amount,
COUNT(result_ind.result_id) games,
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
LEFT JOIN division_dues ON d_division=tp_division AND d_player=tp_player AND d_team=tp_team
LEFT JOIN division_payment ON dp_division_dues=d_id
WHERE team_player.tp_team='{$_GET['team_id']}' AND team_player.tp_division='{$_GET['division_id']}'
AND NOT player.first_name LIKE '%FORFEIT%' AND NOT player.first_name LIKE '%HANDICAP%'
AND NOT player.last_name LIKE '%FORFEIT%' AND NOT player.last_name LIKE '%HANDICAP%'
GROUP BY tp_player ORDER BY percentage DESC");

}
if ($leagueID == '4') {
	$view->parse('main.scrambleheader');
}
else {
	$view->parse('main.playerheader');
}

while($row = mysql_fetch_assoc($result))
{
	foreach($row as $key => $val)
	$view->assign($key, $val);

	if ($leagueID  == '4'){
		$view->parse('main.scrambledata');
	}
	else {
		$view->parse('main.playerdata');
	}
}

$view->parse('main');
$view->out('main');

?>