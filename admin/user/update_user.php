<?php

include '../../include/mysql.php';

if (isset($_GET['password']))
	$set = "password='".urldecode($_GET['password'])."', ";
	
$set .= 
"player_group='"            . addslashes(urldecode($_GET['player_group'])) . "', " . 
"player_login='"            . addslashes(urldecode($_GET['player_login'])) . "', " . 
"first_name='"              . addslashes(urldecode($_GET['first_name'])) . "', " .
"last_name='"               . addslashes(urldecode($_GET['last_name'])) . "', " .
"street1='"                 . addslashes(urldecode($_GET['street1'])) . "', " . 
"street2='"                 . addslashes(urldecode($_GET['street2'])) . "', " .
"city='"                    . addslashes(urldecode($_GET['city'])) . "', " .
"state='"                   . addslashes(urldecode($_GET['state'])) . "', " . 
"zip='"                     . addslashes(urldecode($_GET['zip'])) . "', " .
"phone='"                   . addslashes(urldecode($_GET['phone'])) . "', " .
"handicap='"                . addslashes(urldecode($_GET['handicap'])) . "', " .
"handicap_eight='"          . addslashes(urldecode($_GET['handicap_eight'])) . "', " .
"handicap_straight='"       . addslashes(urldecode($_GET['handicap_straight'])) . "', " . 
"handicap_mixed_8='"        . addslashes(urldecode($_GET['handicap_mixed_8'])) . "', " .
"handicap_mixed_9='"        . addslashes(urldecode($_GET['handicap_mixed_9'])) . "', " .
"handicap_eight_beginner='" . addslashes(urldecode($_GET['handicap_eight_beginner'])) . "', " .
"handicap_10='"             . addslashes(urldecode($_GET['handicap_10'])) . "', " .
"email='"        			. addslashes(urldecode($_GET['email'])) ."'";

mysql_query("UPDATE player SET {$set} WHERE player_id='{$_GET['player_id']}'");

if (mysql_affected_rows() < 1)
	echo mysql_error() . $set;
else
	echo '1';
?>