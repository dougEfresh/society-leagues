<?php

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('bca_stats_home.html');


			
$view->parse('main');
$view->out('main');

?>