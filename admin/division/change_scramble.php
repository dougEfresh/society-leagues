<?php

include '../../include/mysql.php';
include '../../include/functions.php';

ini_set('display_errors',1); 
 error_reporting(E_ALL);

$result = mysql_query("
UPDATE match_schedule SET scramble9={$_GET['scramble9']}
WHERE match_id ={$_GET['match_id']}");

/*

echo mysql_affected_rows();
*/
?>