<?php
session_start();

$_SESSION['division_filter'] = urldecode($_GET['division_filter']);

?>