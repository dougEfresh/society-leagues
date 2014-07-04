<?php
session_start();

if ( (isset($_COOKIE['player_id'])) || (isset($_SESSION['player_id'])))
	header("Location:index.php?v=1");

include 'include/xtemplate.class.php';

$view = new xtemplate('system/login.html');

if(isset($_GET['message']))
{
	switch($_GET['message'])
	{
		case 'bad_login':
			$view->assign('message', 'Login Failed');
		break;
		
		case 'bad_email':
			$view->assign('message', "The email address {$_GET['email']} was not found.");
		break;
		
		case 'password_reset':
			$view->assign('message', "A new password has been sent to {$_GET['email']}");
		break;
	}
}

$view->parse('main');
$view->out('main');

?>
