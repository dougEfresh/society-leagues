<?php

include '../../include/mysql.php';

die();

mysql_query("DELETE FROM player WHERE player_id='{$_GET['player_id']}'");

echo mysql_affected_rows();

?>