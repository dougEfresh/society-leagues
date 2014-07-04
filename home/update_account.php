<?php

include '../../include/mysql.php';

if (isset($_GET['password']))
	$set = "password='".urldecode($_GET['password'])."', ";
	
$set .= 
"first_name='"              . addslashes(urldecode($_GET['first_name'])) . "', " .
"last_name='"               . addslashes(urldecode($_GET['last_name'])) . "', " .
"street1='"                 . addslashes(urldecode($_GET['street1'])) . "', " . 
"street2='"                 . addslashes(urldecode($_GET['street2'])) . "', " .
"city='"                    . addslashes(urldecode($_GET['city'])) . "', " .
"state='"                   . addslashes(urldecode($_GET['state'])) . "', " . 
"zip='"                     . addslashes(urldecode($_GET['zip'])) . "', " .
"phone='"                   . addslashes(urldecode($_GET['phone'])) . "', " .
"email='"        			. addslashes(urldecode($_GET['email'])) ."'";

mysql_query("UPDATE player SET {$set} WHERE player_id='{$_GET['player_id']}'");

if (mysql_affected_rows() < 1)
	echo mysql_error() . $set;
else
	echo '1';
?>