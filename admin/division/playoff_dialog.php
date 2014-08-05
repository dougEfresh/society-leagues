<?php

include '../../include/mysql.php';
include '../../include/xtemplate.class.php';

$view = new xtemplate('playoff_dialog.html');

$view->assign('division_id', $_GET['division_id']);

$result = mysql_query("
SELECT  * 
FROM playoff_types");

echo mysql_error();



while ($row = mysql_fetch_assoc($result))
	{

		foreach($row as $key => $val)
			$view->assign($key, $val);
		$view->parse("main.playoff_id");
}

$view->parse('main');
$view->out('main');

?>