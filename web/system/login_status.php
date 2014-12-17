<?php
session_start(); 

if ((!isset($_COOKIE['player_id'])) && (!isset($_SESSION['player_id'])))
	header('Location:login.php?v=1'); 
else 
{
	if (isset($_COOKIE['player_id']))
	{
		$_SESSION['player_id'] = $_COOKIE['player_id'];
		$_SESSION['full_name'] = $_COOKIE['full_name'];
		$_SESSION['first_name'] = $_COOKIE['first_name'];
		$_SESSION['last_name'] = $_COOKIE['last_name'];
	}
	else
	{
		setcookie('player_id', $_SESSION['player_id'], time() + (86400 * 14));
		setcookie('full_name', $_SESSION['full_name'], time() + (86400 * 14));
		setcookie('first_name', $_SESSION['first_name'], time() + (86400 * 14));
		setcookie('last_name', $_SESSION['last_name'], time() + (86400 * 14));
	}
}
?>