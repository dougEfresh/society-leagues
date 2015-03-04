<?php
session_start();

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('user_home.html');

foreach($_SESSION as $key => $val)
	$view->assign($key, $val);
	
$view->parse('main');
$view->out('main');

?>