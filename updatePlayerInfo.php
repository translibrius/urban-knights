<?php
require "conn.php";
$id = $_POST["id"];

$sql = "SELECT gold, COUNT(ownerId) FROM users INNER JOIN sectors ON users.id = sectors.ownerId WHERE users.id = '$id';";
$result = mysqli_query($conn, $sql);

if (mysqli_num_rows($result) > 0){ //Jeigu yra toks vartotojas
	$row = mysqli_fetch_row($result);
	$response = array();
	$gold = $row[0];
	$teritorijuCount = $row[1];
	$code = "success";
	$message = "Successfully updated user info!";
	array_push($response, array("code"=>$code, "message"=>$message, "gold"=>$gold, "count"=>$teritorijuCount));
	echo json_encode($response);
} else { //Jei nera tokio vartotojo
	$code = "fail";
	$message = "Error when trying to update user info! (php)";
	array_push($response, array("code"=>$code, "message"=>$message));
	echo json_encode($response);
}
?>