<?php
require "conn.php";
$pid = $_POST["id"];
$sid = $_POST["sectorId"];
//v1.1.1 Added dragons script
$sql = "UPDATE users, sectors SET users.gold = users.gold + 10000, sectors.dragon = '0' WHERE users.id = '$pid' AND sectors.sectorId = '$sid';";
$sql2 = "INSERT INTO actionLogs(user1Id, action, sectorId, time, parameters) VALUES ('$pid', 'Dragon', '$sid', now(), '');";
mysqli_query($conn, $sql2);
$result = mysqli_query($conn, $sql);
$response = array();

if (mysqli_num_rows($result) > 0){ //Jeigu yra toks vartotojas
	$code = "success";
	array_push($response, array("code"=>$code));
	echo json_encode($response);
} else { //Jei nera tokio vartotojo
	$code = "fail";
	array_push($response, array("code"=>$code));
	echo json_encode($response);
}
?>