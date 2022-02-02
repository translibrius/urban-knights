<?php
$db_name = "urbanknights";
$mysql_username = "root";
$mysql_password = "albatrosas69";
$server_name = "localhost";
$conn = mysqli_connect($server_name, $mysql_username, $mysql_password, $db_name);
if($conn){
	//echo "Connection succesful";
	//echo "<br>";
} else {
	//echo "Connection failed \n";
	//echo "<br>";
}
?>
