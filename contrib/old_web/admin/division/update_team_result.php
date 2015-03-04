<?php

include '../../include/mysql.php';
include '../../include/functions.php';

$matchid = $_GET['match_id'];
$htid = $_GET['ht_id'];
$vtid = $_GET['vt_id'];

$htr = ($_GET['ht_r'] == 'WIN' ? '1' : '0');
$vtr = ($_GET['vt_r'] == 'WIN' ? '1' : '0');

mysql_query("UPDATE result_team SET is_win='$htr' WHERE match_id='$matchid' AND team_id = $htid");
mysql_query("UPDATE result_team SET is_win='$vtr' WHERE match_id='$matchid' AND team_id = $vtid");

echo mysql_error();

?>