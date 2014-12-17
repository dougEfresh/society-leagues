<?php
include '../../include/mysql.php';

$value = urldecode($_GET['value']);

if ($value == '')
	die('2');

$result = mysql_query("SELECT * FROM player WHERE {$_GET['field']}='{$value}'");

echo mysql_num_rows($result);

?>