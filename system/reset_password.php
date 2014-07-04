<?php
session_start();

error_reporting(E_ALL);
ini_set('display_errors', 1);

include '../include/mysql.php';
include 'emailer.php';

$p1[] = 'strawberry';
$p1[] = 'orange';
$p1[] = 'cherry';
$p1[] = 'apple';
$p1[] = 'grape';
$p1[] = 'lemon';
$p1[] = 'blueberry';
$p1[] = 'raspberry';
$p1[] = 'chocolate';

$p2[] = 'muffin';
$p2[] = 'pie';
$p2[] = 'icecream';
$p2[] = 'candy';
$p2[] = 'donut';

if ($_POST['action'] == 'reset_password')
{
	$result = mysql_query("SELECT * FROM player WHERE email='{$_POST['email']}'");

	if (mysql_num_rows($result) > 0)
	{
		$user = mysql_fetch_assoc($result);
			
		$passwd = $p1[rand(0, count($p1) - 1)].'-'.$p2[rand(0, count($p2) - 1)].'-'.rand(100,999);
		
		mysql_query("UPDATE player SET password='{$passwd}' WHERE player_id='{$user['player_id']}'");
		
		$text =
		"{$user['first_name']}:\r\n\r\n" . 
		"Your password for {$_SERVER['HTTP_HOST']} has been reset.\r\n\r\n" .
		"LOGIN: {$user['email']}\r\n" . 
		"PASSWORD: {$passwd}\r\n" . 
		"URL: {$_SERVER['HTTP_HOST']}\r\n\r\n\r\n" .
		"This is an automated email so please do not reply back.";
		
		$subject = "Password reset for {$_SERVER['HTTP_HOST']}";
		
		send_text_email($_POST['email'], 'noreply@societybilliards.com', $subject, $text);

		header("Location:../login.php?message=password_reset&email={$_POST['email']}");
	}
	else
	{
		header("Location:../login.php?message=bad_email&email={$_POST['email']}");
	}
}
else
	header('Location:../login.php');
?>