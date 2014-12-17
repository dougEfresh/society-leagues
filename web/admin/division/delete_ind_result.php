<?php

include '../../include/mysql.php';
include '../../include/functions.php';

mysql_query("DELETE FROM result_ind WHERE match_id='{$_GET['match_id']}' AND match_number='{$_GET['match_number']}'");

$result = mysql_query("SELECT * FROM result_ind WHERE match_id='{$_GET['match_id']}'");

if(mysql_num_rows($result) < 1)
	mysql_query("DELETE FROM result_team WHERE match_id='{$_GET['match_id']}'");

?>