<?php

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('new_team.html');


$result = mysql_query("SELECT * FROM league");

while ($row = mysql_fetch_assoc($result))
{
	foreach($row as $key => $val)
		$view->assign($key, $val);
		
	$view->parse('main.league');	
}

$view->parse('main');
$view->out('main');

?>