<?php





session_start();

include '../../include/mysql.php';

//mysql_query("CREATE TEMPORARY TABLE IF NOT EXISTS mt AS (SELECT * FROM match_schedule WHERE division_id='41')");
//mysql_query("CREATE TEMPORARY TABLE IF NOT EXISTS mt AS (SELECT * FROM match_schedule WHERE division_id='41')");


$result = mysql_query("SELECT 

team.`name`,

SUM(result_team.is_win) team_wins,

(SELECT SUM(is_win) FROM result_ind WHERE result_ind.team_id=division_member.team_id AND result_ind.match_id 
IN(SELECT match_id FROM match_schedule WHERE division_id='41')) player_match_wins,

(SELECT SUM(games_won) FROM result_ind WHERE result_ind.team_id=division_member.team_id AND result_ind.match_id 
IN(SELECT match_id FROM match_schedule WHERE division_id='41')) player_games_won

FROM division_member 

JOIN team ON team.team_id=division_member.team_id
JOIN match_schedule ON match_schedule.division_id=division_member.division_id AND (match_schedule.home_team_id=division_member.team_id OR match_schedule.visit_team_id=division_member.team_id)
JOIN result_team ON result_team.team_id=division_member.team_id AND result_team.match_id=match_schedule.match_id

WHERE division_member.division_id='41'
GROUP BY division_member.team_id
ORDER BY team_wins DESC, player_match_wins DESC, player_games_won DESC");


while($row = mysql_fetch_assoc($result))
{
	echo $row['name'] . '<br>';	
	print_r($row);
}


echo '1' . mysql_affected_rows();
?>