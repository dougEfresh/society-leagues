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

$htm = $_GET['htm'];
$vtm = $_GET['vtm'];

$htr = ($_GET['htr'] == 'WIN' ? '1' : '0');
$vtr = ($_GET['vtr'] == 'WIN' ? '1' : '0');

$result = mysql_query("SELECT * FROM match_schedule, player WHERE match_id='{$_GET['match_id']}' AND player_id='{$hp1}'");
$p1 = mysql_fetch_assoc($result);

$result = mysql_query("SELECT * FROM match_schedule, player WHERE match_id='{$_GET['match_id']}' AND player_id='{$vp1}'");
$v1 = mysql_fetch_assoc($result);

switch($p1['league_id'])
{
	case '1':
		$hc = 'hc_9';
		$team = true;
	break;
	
	case '2':
		$hc = 'hc_8';
		$team = true;
	break;
	
	case '3':
	case '5':
		$hc = 'hc_straight';
		$team = false;
	break;
	
	case '4':
		switch($_GET['match_number'])
		{
			case '3':
			case '4':
			case '9':
			case '16':
			case '19':
			case '21':
			case '25':
			case '26':
			case '31':
				$hc = 'hc_m9';
			break;
			
			default:
				$hc = 'hc_m9';
			break;	
		}
		$team = true;
	break;
	
	case '6':
		$hc = 'hc_8Begin';
		$team = true;
	break;
}

//echo $_GET['dbl'];
		
mysql_query("INSERT INTO result_ind SET player_id='{$hp1}', team_id='{$htm}', games_won='{$hs1}', player_handicap='". $p1[$hc] ."',
			games_lost='{$vs1}', is_win='{$hr1}', match_id='{$_GET['match_id']}', match_number='{$_GET['match_number']}'");
			
mysql_query("INSERT INTO result_ind SET player_id='{$vp1}', team_id='{$vtm}', games_won='{$vs1}', player_handicap='". $v1[$hc] ."',
			games_lost='{$hs1}', is_win='{$vr1}', match_id='{$_GET['match_id']}', match_number='{$_GET['match_number']}'");
	
if ($_GET['dbl'] == 'DOUBLES')
{
	$result = mysql_query("SELECT * FROM match_schedule, player WHERE match_id='{$_GET['match_id']}' AND player_id='{$hp2}'");
	$p2 = mysql_fetch_assoc($result);

	$result = mysql_query("SELECT * FROM match_schedule, player WHERE match_id='{$_GET['match_id']}' AND player_id='{$vp2}'");
	$v2 = mysql_fetch_assoc($result);
	
	mysql_query("INSERT INTO result_ind SET player_id='{$hp2}', team_id='{$htm}', games_won='{$hs1}', player_handicap='". $p2[$hc] ."',
			games_lost='{$vs1}', is_win='{$hr1}', match_id='{$_GET['match_id']}', match_number='{$_GET['match_number']}'");

	mysql_query("INSERT INTO result_ind SET player_id='{$vp2}', team_id='{$vtm}', games_won='{$vs1}', player_handicap='". $v2[$hc] ."',
			games_lost='{$hs1}', is_win='{$vr1}', match_id='{$_GET['match_id']}', match_number='{$_GET['match_number']}'");

}

if ($team)
{
	$result = mysql_query("SELECT * FROM result_team WHERE match_id='{$_GET['match_id']}'");
	
	if (mysql_num_rows($result) < 2)
	{
		echo 'Create';	
		mysql_query("INSERT INTO result_team SET team_id='{$htm}', match_id='{$_GET['match_id']}', is_win='{$htr}'");
		mysql_query("INSERT INTO result_team SET team_id='{$vtm}', match_id='{$_GET['match_id']}', is_win='{$vtr}'");
	}
	else
	{
		echo 'Update';
		mysql_query("UPDATE result_team SET is_win='{$htr}' WHERE team_id='{$htm}' AND match_id='{$_GET['match_id']}'");
		mysql_query("UPDATE result_team SET is_win='{$vtr}' WHERE team_id='{$vtm}' AND match_id='{$_GET['match_id']}'");
		echo mysql_affected_rows();
		echo "UPDATE result_team SET is_win='{$vtr}' WHERE team_id='{$vtm}' AND match_id='{$_GET['match_id']}'";
		echo "UPDATE result_team SET is_win='{$htr}' WHERE team_id='{$htm}' AND match_id='{$_GET['match_id']}'";
	}
}

/*
echo "INSERT INTO result_ind SET player_id='{$hp1}', team_id='{$htm}', games_won='{$hs1}', player_handicap='". $p1[$hc] ."',
			games_lost='{$vs1}', is_win='{$hr1}', match_id='{$_GET['match_id']}', match_number='{$_GET['match_number']}'";
*/
//echo mysql_error();

//echo mysql_affected_rows();
?>