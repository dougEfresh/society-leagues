<?php
session_start();

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('users.html');

if ( (isset($_SESSION['user_filter'])) && (strlen($_SESSION['user_filter']) > 0))
	$where = "WHERE first_name LIKE '{$_SESSION['user_filter']}%' OR last_name LIKE '{$_SESSION['user_filter']}%'";
else
	$where = '';
	
$result = mysql_query("SELECT * FROM player {$where} ORDER BY first_name");

while ($row = mysql_fetch_assoc($result))
{
	foreach ($row as $key => $val)
		$view->assign($key, $val);
		
	$view->parse('main.user');		
}
	
$view->parse('main');
$view->out('main');

?>