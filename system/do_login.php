<?php
require_once('../include/session/sessionHandler.php');
$session = new MySqlSessionHandler();

// add db data
$session->setDbDetails('localhost', 'league', null, 'league');
$session->setDbTable('session_handler');
session_set_save_handler(array($session, 'open'),
                         array($session, 'close'),
                         array($session, 'read'),
                         array($session, 'write'),
                         array($session, 'destroy'),
                         array($session, 'gc'));
// The following prevents unexpected effects when using objects as save handlers.
register_shutdown_function('session_write_close');

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
	$sql = sprintf("REPLACE INTO session_map VALUES ('%s','%s')",
				    $user['player_id'] ,
				    session_id());
	if (!(mysql_query($sql))) {
		   error_log("Could not exec : "  + $sql);
           throw new Exception(mysql_error());
    }

	header('Location:../index.php?v=1');
}
else
{
	header('Location:../login.php?message=bad_login&v=1');
}

?>