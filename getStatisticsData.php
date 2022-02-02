<?php
require "conn.php";
$id = $_POST["id"];

$sql = "SELECT users.username, COUNT(1) FROM sectors INNER JOIN users ON users.id = sectors.ownerId WHERE ownerId != '1' GROUP BY ownerId ORDER BY COUNT(1) DESC, lastMapRefresh DESC LIMIT 5";
$stmt = $conn->prepare($sql);
$stmt->execute();

$response = array();
$stmt->bind_result($username, $count);
while($stmt->fetch()){ 
	$temp = [
		'user'=>$username,
		'count'=>$count
	];
	array_push($response, $temp);
}

//displaying the data in json format 
echo json_encode($response);
?>