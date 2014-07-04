<?php

include '../include/mysql.php';
include '../include/xtemplate.class.php';

$view = new xtemplate('uploader.html');

$view->parse('main');
$view->out('main');

?>