<?php

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('new_user.html');

$view->parse('main');
$view->out('main');

?>