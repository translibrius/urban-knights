<?php

require "conn.php";

$id = $_POST["id"];
$isInBounds = $_POST["isInBounds"]; //1.1.0

$sql = "UPDATE users SET users.lastMapRefresh = now() WHERE users.id = '$id'"; //Irasom kad atrefreshintas mapas

if($isInBounds = "true"){ //1.1.0
	mysqli_query($conn, $sql);
}



$sql = "SELECT sectors.sectorId, sectors.ownerId, sectors.level, sectors.captureTime, sectors.dragon, users.color FROM users INNER JOIN sectors ON sectors.ownerId = users.id ORDER BY `sectors`.`sectorId` ASC";//v1.1.1 dragons

$stmt = $conn->prepare($sql);

$stmt->execute();



$response = array();

$stmt->bind_result($id, $ownerId, $level, $captureTime, $dragon, $color);//v1.1.1 dragons

while($stmt->fetch()){ 

	$temp = [

		'id'=>$id,

		'ownerId'=>$ownerId,

		'level'=>$level,

		'captureTime'=>$captureTime,

		'dragon' => $dragon,//v1.1.1 dragons

		'color'=>$color

	];

	array_push($response, $temp);

}



//displaying the data in json format 

echo json_encode($response);

?>