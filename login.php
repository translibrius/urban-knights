<?php
require "conn.php";
$user_name = $_POST["username"];
$user_pass = $_POST["password"];
//SELECT COUNT(ownerId) FROM sectors WHERE sectors.ownerId = '7'
$sql = "SELECT * FROM users WHERE username = '$user_name'";
$result = mysqli_query($conn, $sql);
$response = array();
if (mysqli_num_rows($result) > 0){ //Jeigu yra toks vartotojas
	$row = mysqli_fetch_row($result);
	$id = $row[0];
	$user_neim = $row[1];
	$pw = $row[2];
	$sector = $row[3];
	$gold = $row[4];
	$color = $row[5];
	$place1 = $row[6];
	$place2 = $row[7];
	$place3 = $row[8];
	$lastRefresh = $row[9];

	$sql = "SELECT COUNT(ownerId) FROM sectors INNER JOIN users ON sectors.ownerId = users.id WHERE users.id = '$id';";
	$result = mysqli_query($conn, $sql);
	$row = mysqli_fetch_row($result);
	$count = $row[0];
	
	if ($pw == $user_pass) { //Jei abu atitinka
		$code = "success";
		$message = "Successful login";
		array_push($response, array("code"=>$code, "message"=>$message, "id"=>$id, "username"=>$user_neim, "sector"=>$sector, "gold"=>$gold, "place1"=>$place1, "place2"=>$place2, "place3"=>$place3, "lastRefresh"=>$lastRefresh, "password"=>$pw, "color" =>$color, "count"=>$count));
		echo json_encode($response);
	} else { //Jei netinka slaptazodis
		$code = "fail";
		$message = "Neteisingas slaptažodis.";
		array_push($response, array("code"=>$code, "message"=>$message));
		echo json_encode($response);
	}
} else { //Jei nera tokio vartotojo
	$code = "fail";
	$message = "Jūsų įvestas vartotojas nerastas.";
	array_push($response, array("code"=>$code, "message"=>$message));
	echo json_encode($response);
}
?>