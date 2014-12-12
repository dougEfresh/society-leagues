<?php
session_start();

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('divisions.html');

if ( (isset($_SESSION['division_filter'])) && (strlen($_SESSION['division_filter']) > 0))
	$where = "WHERE (league_game LIKE '%{$_SESSION['division_filter']}%' OR league_type LIKE '%{$_SESSION['division_filter']}%' 
				OR season_name.sn_name LIKE '%{$_SESSION['division_filter']}%' OR start_date LIKE '%{$_SESSION['division_filter']}%')";
else
	$where = '';
	
$result = mysql_query("SELECT
division.division_id, 
DATE_FORMAT(division.start_date,'%M %D %Y') start_date, 
IF(division.time_slot=1,'Early','Late') time_slot, days.d_name division_day,
league.league_id, CONCAT(league.league_type,' ',league.league_game) league_name,
season.season_id, CONCAT(season_name.sn_name,' - ',season.season_year) season_name
FROM division
RIGHT JOIN league ON league.league_id=division.league_id
RIGHT JOIN season ON season.season_id=division.season_id
RIGHT JOIN days ON  d_id=division.division_day
RIGHT JOIN season_name ON season_name.sn_id=season.season_number
{$where} ORDER BY division.start_date DESC");

while ($row = mysql_fetch_assoc($result))
{
	foreach ($row as $key => $val)
		$view->assign($key, $val);
		
	$view->parse('main.division');
}

$view->parse('main');
$view->out('main');

?>