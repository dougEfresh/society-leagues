<?php

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('user_history.html');

$result = mysql_query("SELECT * FROM player WHERE player_id='{$_GET['player_id']}'");

$row = mysql_fetch_assoc($result);

foreach ($row as $key => $val)
	$view->assign($key, $val);
	
$result = mysql_query("SELECT * FROM groups");

while($group = mysql_fetch_assoc($result))
{
	foreach($group as $key => $val)
		$view->assign($key, $val);
		
	if ($row['player_group'] == $group['g_id'])
		$view->assign('checked', 'checked');
	else
		$view->assign('checked', '');	
		
	$view->parse('main.group');	
}
			
$view->parse('main');
$view->out('main');

?>