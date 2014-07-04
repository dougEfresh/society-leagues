<?php

include '../include/mysql.php';
include '../include/xtemplate.class.php';

$view = new xtemplate('progress_row.html');

$unique = urldecode($_GET['unique']);
$filename = urldecode($_GET['file']);

$view->assign('filename', $filename);
$view->assign('unique', $unique);

$view->parse('main');
$view->out('main');

die();
?>