<?php
session_start();

$_SESSION['team_filter'] = urldecode($_GET['team_filter']);

?>