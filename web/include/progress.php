<?php
session_start();

include '../include/mysql.php';
include '../include/xtemplate.class.php';

$view = new xtemplate('progress.html');

$view->assign('message', $_GET['message']);
	
$view->parse('main');
$view->out('main');

?>