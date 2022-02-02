<?php
require "conn.php";
$versija = $_POST["version"];

$sql = "SELECT number FROM versions;";

$result = mysqli_query($conn, $sql);
$response = array();

//echo $result." ".$versija;

if (mysqli_num_rows($result) > 0){
	$row = mysqli_fetch_row($result);
	$version = $row[0];

	if($versija == $version){
		$code = "success";
		array_push($response, array("code"=>$code, "version"=>$version, "versionClient"=>$versija));
		echo json_encode($response);
	} else {
		$code = "fail";
		array_push($response, array("code"=>$code, "version"=>$version, "versionClient"=>$versija));
		echo json_encode($response);
	}
} else {
	$code = "fail";
	array_push($response, array("code"=>$code, "version"=>$version, "versionClient"=>$versija));
	echo json_encode($response);
}
//mysql_close($conn);
?>