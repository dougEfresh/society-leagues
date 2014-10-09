<?php

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('change_scramble_dialog.html');

$view->assign('match_id', $_GET['match_id']);

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