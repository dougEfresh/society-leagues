<?php

die('disabled');

error_reporting(E_ALL);
ini_set('display_errors', 1);

$conn = mysql_connect('localhost', 'daleegdbuser', 'pr3dat0r');

$result = mysql_query("SELECT * FROM daleeg.player");

while($row = mysql_fetch_assoc($result))
{
	$result_new = mysql_query("SELECT * FROM league.player WHERE player_id='{$row['player_id']}'");
	
	if (mysql_num_rows($result_new) > 0)
	{
		$new_row = mysql_fetch_assoc($result_new);
		
		mysql_query("UPDATE league.player SET
						handicap='{$new_row['handicap']}',
						handicap_eight='{$new_row['handicap_eight']}',
						handicap_straight='{$new_row['handicap_straight']}',
						handicap_mixed_8='{$new_row['handicap_mixed_8']}',
						handicap_mixed_9='{$new_row['handicap_mixed_9']}',
						handicap_eight_beginner='{$new_row['handicap_eight_beginner']}',
						handicap_10='{$new_row['handicap_10']}',
						handicap_eight_beginner={$new_row['email']}'
						WHERE player_id='{$new_row['player_id']}'");
						
		echo $new_row['first_name'] . ' ' . $new_row['last_name'] . mysql_affected_rows() . '<br>';				
	}

}


?>