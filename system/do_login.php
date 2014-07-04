<?php
session_start();

include '../include/mysql.php';

$result = mysql_query("SELECT * FROM player WHERE player_login='{$_POST['login']}' AND password='{$_POST['password']}'");

if (mysql_num_rows($result) > 0)
{
	$user = mysql_fetch_assoc($result);	
	$_SESSION['player_id'] = $user['player_id'];
	$_SESSION['full_name'] = $user['first_name'].' '.$user['last_name'];
	$_SESSION['first_name'] = $user['first_name'];
	$_SESSION['last_name'] = $user['last_name'];
	header('Location:../index.php?v=1');
}
else
{
	header('Location:../login.php?message=bad_login&v=1');
}

?>