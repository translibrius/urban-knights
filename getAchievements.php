<?php

require "conn.php";



$sql = "SELECT users.username, users.place1, users.place2, users.place3 FROM users WHERE users.id != '1' ORDER BY place1 DESC, place2 DESC, place3 DESC, lastMapRefresh DESC LIMIT 10";

$stmt = $conn->prepare($sql);

$stmt->execute();



$response = array();

$stmt->bind_result($username, $place1, $place2, $place3);

while($stmt->fetch()){ 

	$temp = [

		'username'=>$username,

		'place1'=>$place1,

		'place2'=>$place2,

		'place3'=>$place3

	];

	array_push($response, $temp);

}



//displaying the data in json format 

echo json_encode($response);

?>