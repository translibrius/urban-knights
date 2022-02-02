<?php
require "conn.php";
$id = $_POST["id"];
//v1.1.1 Added profile script
$sql = "SELECT sum(case when actionLogs.action = 'Capt' then 1 else 0 end) AS captCount,
       sum(case when actionLogs.action = 'SChng' then 1 else 0 end) AS changeCount,
       sum(case when actionLogs.action = 'Upgrd' then 1 else 0 end) AS upgrCount,
       sum(case when actionLogs.action = 'Dragon' then 1 else 0 end) AS dragCount,
       sum(case when actionLogs.action = 'Capt' AND actionLogs.parameters = '0' then 1 else 0 end) AS uniqueCount FROM actionLogs WHERE actionLogs.user1Id = '$id'";
$stmt = $conn->prepare($sql);
$stmt->execute();

$response = array();
$stmt->bind_result($captCount, $changeCount, $upgrCount, $dragCount, $uniqueCount);
while($stmt->fetch()){ 
	$temp = [
		'captCount'=>$captCount,
		'changeCount'=>$changeCount,
		'upgrCount'=>$upgrCount,
		'dragonCount'=>$dragCount,
		'uniqueCount'=>$uniqueCount
	];
	array_push($response, $temp);
}

//displaying the data in json format 
echo json_encode($response);
?>