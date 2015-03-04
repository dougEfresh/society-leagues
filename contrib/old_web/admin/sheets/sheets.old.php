<?php

session_start();

include '../../include/mysql.php';

$result = mysql_query("
SELECT * FROM match_schedule
JOIN division ON division.division_id=match_schedule.division_id
JOIN league ON league.league_id=division.league_id 
WHERE match_schedule.division_id='{$_GET['division_id']}' AND match_schedule.match_number='{$_GET['week']}'");

$row = mysql_fetch_assoc($result);

$league_id = $row['league_id'];

switch($row['league_id'])
{
	case '4':
		$path				= 'background/mixed.jpg';
		$handicap_DB		= 'handicap_mixed_9';
		$page['width']		= 612;
		$page['height']		= 792;
		$back['x']			= 30;
		$back['y']			= -240;
		$back['width']		= 550;
		$back['height']		= 1000;
		$player_page		= 1;
		$player_start_row	= 663;
		$player_spacing		= 2;
		$player_font_size	= 9;
		$player['home']		= $home_team_column = 40;
		$player['visit']	= $visit_team_column = 355;
		$team_row			= 690;
		$date['column']		= 35;
		$date['row']		= 717;
	break;
	
	case '3':
		die('This league requires no scoresheet');
	break;
			
	case '2':
		$path				= 'background/8-ball.jpg';
		$handicap_DB		= 'handicap_eight';
		$page['width']		= 612;
		$page['height']		= 792;
		$back['x']			= 0;
		$back['y']			= 20;
		$back['width']		= 612;
		$back['height']		= 782;		
		$player_page		= 2;
		$player_start_row	= 663;
		$player_spacing		= 2;
		$player_font_size	= 9;
		$home_team_column	= 60;
		$visit_team_column	= 335;
		$player['home']		= 110;
		$player['visit']	= 365;
		$team_row			= 710;
		$date['column']		= 335;
		$date['row']		= 680;		
	break;

	case '6':
		$path				= 'background/8-ball.jpg';
		$handicap_DB		= 'handicap_eight_beginner';
		$page['width']		= 612;
		$page['height']		= 792;
		$back['x']			= 0;
		$back['y']			= 20;
		$back['width']		= 612;
		$back['height']		= 782;		
		$player_page		= 2;
		$player_start_row	= 663;
		$player_spacing		= 2;
		$player_font_size	= 9;
		$home_team_column	= 60;
		$visit_team_column	= 335;
		$player['home']		= 110;
		$player['visit']	= 365;
		$team_row			= 710;
		$date['column']		= 335;
		$date['row']		= 680;		
	break;
				
	case '1':
		$path				= 'background/9-ball.jpg';
		$handicap_DB		= 'handicap';
		$page['width']		= 792;
		$page['height']		= 612;
		$back['x']			= 0;
		$back['y']			= 20;
		$back['width']		= 782;
		$back['height']		= 612;	
		$player_page		= 1;
		$player_start_row	= 480;
		$player_spacing		= 10.3;
		$player_font_size	= 12;
		$home_team_column	= 90;
		$visit_team_column	= 445;
		$player['home']		= 98;
		$player['visit']	= 455;
		$team_row			= 507;
		$date['column']		= 90;
		$date['row']		= 520;				
	break;
			
	default:
		die('This league requires no scoresheet');
	break;
}

$result = mysql_query("
SELECT *, DATE_FORMAT(match_schedule.match_start_date, '%W - %M %D') week,
ht.name home_team, vt.name visit_team, ht.team_id home_id, vt.team_id visit_id, league.league_type
FROM match_schedule
LEFT JOIN team ht ON ht.team_id=match_schedule.home_team_id
LEFT JOIN team vt ON vt.team_id=match_schedule.visit_team_id
JOIN division ON division.division_id=match_schedule.division_id
JOIN league ON league.league_id=division.league_id 
WHERE match_schedule.division_id='{$_GET['division_id']}' AND match_schedule.match_number='{$_GET['week']}'");
	
try
{
	$p = new PDFlib();
	$p->begin_document('', "");
	$p->set_parameter("escapesequence", "true");
	$p->set_parameter("kerning", "true");
	$p->set_parameter("SearchPath", $_SERVER['DOCUMENT_ROOT'] . 'admin/sheets/fonts');
}
catch (Exception $e)
{
	die('exception::'.$e);	
}

while($row = mysql_fetch_assoc($result))
{
	$p->begin_page_ext($page['width'], $page['height'], '');

	try
	{
		$option = "matchbox={name=image_pos} boxsize={".$back['width'].' '.$back['height']."} fitmethod=meet position={left top}";
	
		$image = $p->load_image("auto", $path, "");
		$p->fit_image($image, $back['x'], $back['y'], $option);
		$p->close_image($image);
	
		$font = $p->load_font("Helvetica", "winansi", "");
		
		$font_size = 10;
		$p->setfont($font, $font_size);
		$p->set_text_pos($date['column'], $date['row']);
		$p->setcolor("both", "cmyk", 1, 1, 0, 0);
		$p->show('Scheduled Date:  '.$row['week']);
				
		$p->setfont($font, $font_size);
		$p->setcolor("both", "cmyk", 0, 0, 0, 1);
		$p->set_text_pos($home_team_column, $team_row);
		$p->show($row['home_team']);

		$p->setcolor("both", "cmyk", 0, 0, 0, 1);
		$p->set_text_pos($visit_team_column, $team_row);
		$p->show($row['visit_team']);
		
		if ($player_page == 2)
		{
			$p->end_page_ext("");
			$p->begin_page_ext($page['width'], $page['height'], '');
		}
				
		$hp_result = mysql_query("
		SELECT *, CONCAT(player.first_name,' ',player.last_name,' (',handicap_display.hcd_name,')') player_name 
		FROM team_player 
		JOIN player ON player.player_id=team_player.tp_player
		JOIN team ON team.team_id=team_player.tp_team
		LEFT JOIN handicap_display ON (hcd_league={$league_id} AND hcd_handicap={$handicap_DB})
		WHERE 
		team_player.tp_team='{$row['home_id']}' AND 
		team_player.tp_division='{$_GET['division_id']}' AND 
		NOT player.first_name LIKE '%FORFEIT%' AND
		NOT player.last_name LIKE '%FORFEIT%' AND
		NOT player.first_name LIKE '%HANDICAP%' AND
		NOT player.last_name LIKE '%HANDICAP%'");
		
		$x_position = $player_start_row;
		$team_printed = false;
		
		while($hp_row = mysql_fetch_assoc($hp_result))
		{
			if (($player_page == 2) && (!$team_printed))
			{
				$p->setfont($font, 16);
				$p->set_text_pos( $player['home'], $x_position);
				$p->show($hp_row['name']);
				$x_position -= 18;
				$team_printed = true;
			}
		
			$p->setfont($font, $player_font_size);
			$p->set_text_pos( $player['home'], $x_position);
			$p->show($hp_row['player_name']);
			$x_position -= $font_size + $player_spacing;
		}
		
		$hp_result = mysql_query("
		SELECT *, CONCAT(player.first_name,' ',player.last_name,' (',handicap_display.hcd_name,')') player_name 
		FROM team_player 
		JOIN player ON player.player_id=team_player.tp_player
		JOIN team ON team.team_id=team_player.tp_team
		LEFT JOIN handicap_display ON (hcd_league={$league_id} AND hcd_handicap={$handicap_DB})
		WHERE team_player.tp_team='{$row['visit_id']}' AND 
		team_player.tp_division='{$_GET['division_id']}' AND 
		NOT player.first_name LIKE '%FORFEIT%' AND
		NOT player.last_name LIKE '%FORFEIT%' AND
		NOT player.first_name LIKE '%HANDICAP%' AND
		NOT player.last_name LIKE '%HANDICAP%'");
		
		$x_position = $player_start_row;
		$team_printed = false;
		
		while($hp_row = mysql_fetch_assoc($hp_result))
		{
			if (($player_page == 2) && (!$team_printed))
			{
				$p->setfont($font, 16);
				$p->set_text_pos( $player['visit'], $x_position);
				$p->show($hp_row['name']);
				$x_position -= 18;
				$team_printed = true;
			}
						
			$p->setfont($font, $player_font_size);
			$p->set_text_pos( $player['visit'], $x_position);
			$p->show($hp_row['player_name']);
			$x_position -= $font_size + $player_spacing;
		}		
	}
	catch(Exception $e)
	{
		die('exception::' . $e);	
	}
			
	$p->end_page_ext("");
}

$p->end_document("");

$document = $p->get_buffer();

header("Content-Type:application/pdf");
header("Content-Length:" . strlen($document));
header("Content-Disposition:inline; filename=scoresheet.pdf");
echo $document;
?>