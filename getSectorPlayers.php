<?php
require "conn.php";
$id = $_POST["id"];

$sql = "SELECT users.username FROM users WHERE users.sector = '$id' AND TIMESTAMPDIFF(HOUR,users.lastMapRefresh,now()) < 6";
$stmt = $conn->prepare($sql);
$stmt->execute();

$response = array();
$stmt->bind_result($username);
while($stmt->fetch()){ 
	$temp = [
		'username'=>$username
	];
	array_push($response, $temp);
}

//displaying the data in json format 
echo json_encode($response);
?>