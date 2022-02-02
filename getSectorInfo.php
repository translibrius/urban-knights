<?php
require "conn.php";
$id = $_POST["id"];

$sql = "SELECT (SELECT users.username FROM users INNER JOIN sectors ON users.id = sectors.ownerId WHERE sectors.sectorId = '$id'), sectors.level, sectors.dragon, sectors.captureTime + INTERVAL 1 HOUR, now(), weight FROM sectors WHERE sectors.sectorId = '$id';"; //v1.1.0 weight v1.1.1 dragons
$result = mysqli_query($conn, $sql);

if (mysqli_num_rows($result) > 0){ //Jeigu yra toks vartotojas
	$row = mysqli_fetch_row($result);
	$response = array();
	$ownerId = $row[0];
	$level = $row[1];
	$dragon = $row[2];//v1.1.1 Dragons
	$captureTime = $row[3];
	$now = $row[4];
	$weight = $row[5]; //v1.1.0 weight
	$code = "success";
	$message = "Successfully selected OnClickSector info!";
	array_push($response, array("code"=>$code, "message"=>$message, "ownerId"=>$ownerId, "level"=>$level, "captureTime"=>$captureTime, "now"=>$now, "weight"=>$weight, "dragon"=>$dragon)); //v1.1.0 weight v1.1.1 Dragons
	echo json_encode($response);
} else { //Jei nera tokio vartotojo
	$code = "fail";
	$message = "Error when trying to select OnClickSector info! (php)";
	array_push($response, array("code"=>$code, "message"=>$message));
	echo json_encode($response);
}
?>