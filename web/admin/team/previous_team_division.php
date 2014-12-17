<?php

include '../../include/mysql.php';

$result = mysql_query("SELECT * FROM team_division WHERE team_id='{$_GET['team_id']}' ORDER BY division_id DESC LIMIT 1");
$row = mysql_fetch_assoc($result);

die($row['division_id']);

?>