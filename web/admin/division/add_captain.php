<?php

include '../../include/mysql.php';
include '../../include/functions.php';

ini_set('display_errors',1); 
 error_reporting(E_ALL);

$result = mysql_query("
UPDATE team SET captain_id={$_GET['captain_id']}
WHERE team_id ={$_GET['team_id']}");

/*

echo mysql_affected_rows();
*/
?>