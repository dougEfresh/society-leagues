<?php

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('change_scramble_dialog.html');

$view->assign('division_id', $_GET['division_id']);
$view->assign('match_number', $_GET['match_number']);


echo mysql_error();



while ($row = mysql_fetch_assoc($result))
	{

		foreach($row as $key => $val)
			$view->assign($key, $val);
		$view->parse("main.match_id");
}

$view->parse('main');
$view->out('main');

?>