<?php
require "conn.php";
$user_id = $_POST["ID"];
$currentSectorID = $_POST["currentSectorID"];
$sectorInt = (int)$currentSectorID; //1.0.1

$sql = "UPDATE users SET users.sector = '$currentSectorID' WHERE users.id = '$user_id';";
$sql2 = "SELECT sectors.ownerId, sectors.level, sectors.captureTime, sectors.dragon FROM sectors WHERE sectors.sectorId = '$currentSectorID';";
if ($sectorInt >= 1 && $sectorInt <= 100){ //1.0.1 1.1.1 dragon
	mysqli_query($conn, $sql);
}
$result = mysqli_query($conn, $sql2);
$response = array();

if (mysqli_num_rows($result) > 0){ //If query worked
	$row = mysqli_fetch_row($result);
	$code = "success";
	$id = $row[0];
	$level = $row[1];
	$captureTime = $row[2];
	$dragon = $row[3]; //1.1.1 dragon
	array_push($response, array("code"=>$code, "ownerID"=>$id, "level"=>$level, "captureTime"=>$captureTime, "dragon"=>$dragon));//1.1.1 dragon
	echo json_encode($response);
} else { //If query failed
	$code = "fail";
	$message = "Query failed";
	array_push($response, array("code"=>$code, "message"=>$message));
	echo json_encode($response);
}
?>