<?php

require "conn.php";

$id = $_POST["id"];



$sql = "SELECT users.username, users.gold FROM users WHERE users.id != '1' ORDER BY users.gold DESC, lastMapRefresh DESC LIMIT 5";

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