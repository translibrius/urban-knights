<?php
	require "conn.php";
	$sql = "SELECT users.sector, now() FROM users WHERE users.id = '3'";
	$result = mysqli_query($conn, $sql);
	$response = array();
	$row = mysqli_fetch_row($result);
	$id = $row[0];
	$now = $row[1];

	$time1 = strtotime($now);
	$time2 = strtotime('2019-07-25 19:53:4');
	$difference = round(abs($time2 - $time1) / 3600,2);
	echo $difference;
?>