<?php
if (getenv('DB_HOST')) {
   $conn = mysql_connect(getenv('DB_HOST'), 'league');
   mysql_select_db('league', $conn);
} else {
   $conn = mysql_connect('localhost', 'league');
   mysql_select_db('league', $conn);
}

?>
