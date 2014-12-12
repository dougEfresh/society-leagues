<?php

include $_SERVER['DOCUMENT_ROOT'].'/system/emailer.php';

$to = urldecode($_GET['email']);

switch($_GET['function'])
{
	case 'test':
		$message =
		"This is a test email to verify the email address we have on file.\r\n\r\n".
		"This is an automated message so do not repond to this message.";
		send_text_email($to, 'noreply@societybilliards.com', 'Test Email 3', $message);
		echo 'test';
	break;
	
	case 'welcome':
		$password = urldecode($_GET['password']);
		$login  = urldecode($_GET['player_login']);
		$message =
		"Welcome to the League at Society Billiards.\r\n".
		"To access the league website, please login to the site using the credentials below.\r\n\r\n".
		"You should change your password on your first login using the 'My Information' tab.\r\n\r\n".
		"The league web site will have your team statistics as well as the current schedule\r\n\r\n\r\n".
		"This is an automated message so do not repond to this message.\r\n\r\n\r\n".
		"LOGIN: {$login}\r\n".
		"PASSWD: {$password}\r\n".
		"URL: {$_SERVER['HTTP_HOST']}";
		
		send_text_email($to, 'noreply@societybilliards.com', 'Welcome to the leagues ay Society Billiards', $message);
		echo 'welcome';
	break;
}

?>