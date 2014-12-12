<?php
function set_select($view, $section, $selected_value = '', $table, $key, $name)
{
	$sres = mysql_query("SELECT * FROM {$table}");
	
	while($srow = mysql_fetch_assoc($sres))
	{
		$view->assign($key, $srow[$key]);
		$view->assign($name, $srow[$name]);
			
		if($selected_value != '')
		{			
			if ($selected_value == $srow[$key])
				$view->assign('selected', 'selected');
			else
				$view->assign('selected', '');
		}
		else
			$view->assign('selected', '');
		
		$view->parse("{$section}.option");
	}
	
	$view->parse("{$section}");
}

function jquery_to_mysql_date($date)
{
	$date = explode('/', $date);
	$date = implode('-', array($date[2], $date[0], $date[1]));
	return $date;	
}

function mysql_to_jquery_date($date)
{
	$date = explode('-', $date);
	$date = implode('/', array($date[1], $date[2], $date[0]));
	return $date;	
}

/*
function round_robin($team_list, $schedule_length)
{
	$round_robin_flip		= 0;
	$round_robin_counter	= 0;
	$week					= 0;
	$schedule				= array();
	
	for ( $z = 0; $z < $schedule_length; $z++ )
	{	
		for ( $s = 0; $s < (count($team_list) - 1); $s += 2 )
		{
			if (($round_robin_flip & 1) == 1)
				$schedule[$week][] = array($team_list[$s],$team_list[$s + 1]);
			else
				$schedule[$week][] = array($team_list[$s + 1],$team_list[$s]);
		}
		
		$temp = $team_list[count($team_list) -1];
			
		for ( $y = (count($team_list) - 1); $y > 1; $y-- )
			$team_list[$y] = $team_list[$y - 1];
			
		$team_list[1] = $temp;
	
		if ($round_robin_counter == (count($team_list) / 2))
		{
			$round_robin_flip++;
			$round_robin_counter = 0;
		}
		else
			$round_robin_counter++;
	
		$week++;
	}
	return $schedule;
}
*/

function round_robin($teams, $schedule_length)
{
	$round_robin_counter	= 0;
	$week					= 0;
	$schedule				= array();
	
	for ($week = 0; $week < $schedule_length ; $week++ )
	{	
		$schedule[$week] = pair($teams);
		$teams = rotate($teams);
		$round_robin_counter++;
	}
	return $schedule;
}

function rotate($team)
{
	$new_array[0] = $team[0];
	$new_array[1] = $team[count($team) - 1];
	unset($team[count($team) - 1]);
	unset($team[0]);
	return array_merge($new_array,$team);
}

function pair($team)
{
	$x = 0;
	$temp = array();
	
	while($x < (count($team) / 2))
	{
		$temp[] = array($team[$x],$team[count($team) - $x - 1]);
		$x++;
	}
	return $temp;
}
?>