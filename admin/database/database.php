<?php
session_start();

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('database.html');

$view->parse('main');
$view->out('main');

?>