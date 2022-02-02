<?php
require "conn.php";
$id = $_POST["id"];

$sql = "SELECT users.sector FROM users WHERE users.id != 1 AND users.id != '$id' AND users.sector != 0 AND TIMESTAMPDIFF(HOUR,users.lastMapRefresh,now()) < 6";
$stmt = $conn->prepare($sql);
$stmt->execute();

$response = array();
$stmt->bind_result($sector);
while($stmt->fetch()){ 
	$temp = [
		'sector'=>$sector
	];
	array_push($response, $temp);
}

//displaying the data in json format 
echo json_encode($response);
?>