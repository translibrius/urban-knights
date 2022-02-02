<?php
require "conn.php";
$user_name = $_POST["username"];
$user_pass = $_POST["password"];

$sql = "SELECT * FROM users WHERE username = '$user_name' AND password = '$user_pass'";

$result = mysqli_query($conn, $sql);
$response = array();

if (mysqli_num_rows($result) > 0){
	$row = mysqli_fetch_row($result);
	$id = $row[0];
	$username = $row[1];
	$password = $row[2];
	$sector = $row[3];
	$gold = $row[4];
	$color = $row[5];
	$place1 = $row[6];
	$place2 = $row[7];
	$place3 = $row[8];
	$lastmaprefresh = $row[9];
	$code = "success";
	array_push($response, array("code"=>$code,"id"=>$id, "username"=>$username, "password"=>$password, "sector"=>$sector, "gold"=>$gold, "color"=>$color, "place1"=>$place1, "place2"=>$place2, "place3"=>$place3, "lastRefresh"=>$lastmaprefresh));
	echo json_encode($response);
} else {
	$code = "fail";
	$message = "User not found, try agane";
	array_push($response, array("code"=>$code, "message"=>$message));
	echo json_encode($response);
}

mysql_close($conn);
?>