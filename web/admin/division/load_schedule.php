<?php

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('load_schedule.html');

$result = mysql_query("
SELECT match_schedule.*, DATE_FORMAT(match_schedule.match_start_date, '%M %D') week, COUNT(result_id) result_count
FROM match_schedule
LEFT JOIN result_ind ON result_ind.match_id=match_schedule.match_id
WHERE match_schedule.division_id='{$_GET['division_id']}' AND match_schedule.match_number>0
GROUP BY match_schedule.match_number");

echo mysql_error();

if (mysql_num_rows($result) > 0)
{
	while ($row = mysql_fetch_assoc($result))
	{
		foreach($row as $key => $val)
			$view->assign($key, $val);
		if ($row['league_id'] == 4 and $row['result_count'] == 0)  {
			$view->parse("main.week.scramble"); 
			if ($row['scramble9'] == 1) { $view->parse("main.week.matchtype9"); }
			else { $view->parse("main.week.matchtype8"); }
			}
		else { 
			if ($row['scramble9'] == 1) { $view->parse("main.week.matchtype9"); }
			else { $view->parse("main.week.matchtype8"); }
			} 			
		if ($row['result_count'] == 0)
			$view->parse("main.week.substitute");

		$view->parse("main.week");
	}
}
else
{
	$view->parse("main.empty");
}

$view->parse('main');
$view->out('main');

?>