<?php

include '../../include/mysql.php';
include '../../include/functions.php';

ini_set('display_errors',1); 
 error_reporting(E_ALL);

$result = mysql_query("
UPDATE match_schedule SET scramble9={$_GET['scramble9']}
WHERE division_id ={$_GET['division_id']} AND match_number = {$_GET['match_number']}");

/*

echo mysql_affected_rows();
*/
?>