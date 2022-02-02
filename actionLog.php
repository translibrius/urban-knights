<?php
require "conn.php";
$user1Id = $_POST["user1Id"];
$action = $_POST["action"];
$sectorId = $_POST["sectorId"];
$user2Id = $_POST["user2Id"];
$time = $_POST["time"];
$parameters = $_POST["parameters"];

$sql = "INSERT INTO actionLogs(user1Id, action, sectorId, user2Id, time, parameters) VALUES ('$user1Id', '$action', '$sectorId', '$user2Id', now(), '$parameters');";
$result = mysqli_query($conn, $sql);

if (mysqli_num_rows($result) > 0){ //Jeigu yra toks vartotojas
	$row = mysqli_fetch_row($result);
	$response = array();
	$code = "success";
	$message = "Successfully inserted into actionLogs!";
	array_push($response, array("code"=>$code, "message"=>$message));
	echo json_encode($response);
} else { //Jei nera tokio vartotojo
	$code = "fail";
	$message = "Error when trying to insert into actionLogs! (php)";
	array_push($response, array("code"=>$code, "message"=>$message));
	echo json_encode($response);
}
?>