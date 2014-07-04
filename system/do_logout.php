<?php
session_start();
unset($_SESSION['player_id']);
setcookie('player_id', null, -1, '/');
setcookie('full_name', null, -1, '/');
setcookie('first_name', null, -1, '/');
setcookie('last_name', null, -1, '/');
header('Location:../login.php?v=1');
?>