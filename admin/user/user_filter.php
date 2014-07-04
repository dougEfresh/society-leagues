<?php
session_start();

$_SESSION['user_filter'] = urldecode($_GET['user_filter']);

?>