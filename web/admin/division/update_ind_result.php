<?php

include '../../include/mysql.php';
include '../../include/functions.php';

$hp1 = $_GET['hp1'];
$hp2 = $_GET['hp2'];
$vp1 = $_GET['vp1'];
$vp2 = $_GET['vp2'];

$hr1 = ($_GET['hr1'] == 'WIN' ? '1' : '0');
$vr1 = ($_GET['vr1'] == 'WIN' ? '1' : '0');

$hs1 = $_GET['hs1'];
$vs1 = $_GET['vs1'];

$rid = explode(':', $_GET['rid']);

echo $_GET['dbl'];

mysql_query("UPDATE result_ind SET player_id='{$hp1}', games_won='{$hs1}', games_lost='{$vs1}', is_win='{$hr1}' WHERE result_id='{$rid[0]}'");
mysql_query("UPDATE result_ind SET player_id='{$vp1}', games_won='{$vs1}', games_lost='{$hs1}', is_win='{$vr1}' WHERE result_id='{$rid[1]}'");

if ($_GET['dbl'] == 'DOUBLE')
{
	mysql_query("UPDATE result_ind SET player_id='{$hp2}', games_won='{$hs1}', games_lost='{$vs1}', is_win='{$hr1}' WHERE result_id='{$rid[2]}'");
	mysql_query("UPDATE result_ind SET player_id='{$vp2}', games_won='{$vs1}', games_lost='{$hs1}', is_win='{$vr1}' WHERE result_id='{$rid[3]}'");
}

echo mysql_error();

?>