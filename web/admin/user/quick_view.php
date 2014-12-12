<?php

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('quick_view.html');

$result = mysql_query("SELECT * FROM player WHERE player_id='{$_GET['player_id']}'");

$row = mysql_fetch_assoc($result);

foreach ($row as $key => $val)
	$view->assign($key, $val);
			
$view->parse('main');
$view->out('main');

?>