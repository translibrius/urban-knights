<?php
require "conn.php";
$pid = $_POST["playerId"];
$sid = $_POST["sectorId"];
$cost = $_POST["cost"];
$lvl = $_POST["lvl"]; //1.0.1

$sql = "UPDATE users, sectors SET users.gold = users.gold - '$cost', sectors.level = sectors.level + 1 WHERE users.id = '$pid' AND sectors.sectorId = '$sid';";
$sql2 = "INSERT INTO actionLogs(user1Id, action, sectorId, time, parameters) VALUES ('$pid', 'Upgrd', '$sid', now(), '$lvl');";//1.0.1
mysqli_query($conn, $sql2); //1.0.1
$result = mysqli_query($conn, $sql);
$response = array();

if (mysqli_num_rows($result) >0){ //Jeigu yra toks vartotojas
	$code = "success";
	array_push($response, array("code"=>$code));
	echo json_encode($response);
} else { //Jei nera tokio vartotojo
	$code = "fail";
	array_push($response, array("code"=>$code));
	echo json_encode($response);
}
?>