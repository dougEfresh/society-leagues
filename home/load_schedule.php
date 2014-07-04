<?php

include '../include/mysql.php';
include '../include/xtemplate.class.php';

$view = new xtemplate('load_schedule.html');

$result = mysql_query("
SELECT *, DATE_FORMAT(match_schedule.match_start_date, '%M %D') week
FROM match_schedule
WHERE match_schedule.division_id='{$_GET['division_id']}' AND match_schedule.match_number>0
GROUP BY match_schedule.match_number");

echo mysql_error();

if (mysql_num_rows($result) > 0)
{
	while ($row = mysql_fetch_assoc($result))
	{
		foreach($row as $key => $val)
			$view->assign($key, $val);
		
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