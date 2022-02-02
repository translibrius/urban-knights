<?php
require "conn.php";
$ownerID = $_POST["ownerID"];
$captureTime = $_POST["captureTime"];
$userID = $_POST["userID"];
$sectorLevel = $_POST["sectorLevel"];
$sectorID = $_POST["sectorID"];

$sql = "SELECT users.sector, now(), TIMESTAMPDIFF(HOUR,users.lastMapRefresh,now()) FROM users WHERE users.id = '$ownerID'";
$sqlActionLog = "INSERT INTO actionLogs(user1Id, action, sectorId, user2Id, time, parameters) VALUES ('$userID', 'Capt', '$sectorID', '$ownerID', now(), '$sectorLevel');"; //1.0.1
$result = mysqli_query($conn, $sql);
$response = array();

if (mysqli_num_rows($result) > 0){ //If query executed
	$row = mysqli_fetch_row($result);
	$id = $row[0];
	$now = $row[1];
	$hourdiff = $row[2];

	$time1 = strtotime($now);
	$time2 = strtotime($captureTime);
	$difference = round(abs($time2 - $time1) / 3600,2);
	if ($id != $sectorID){ //Jei owneris ne current savo sektoriui
		if ($difference > 1) { //Jei praejo daugiau nei valanda nuo lastCapture
			$recievedGold = ($sectorLevel-1)*30; //1.1.0
			$sql = "UPDATE sectors SET sectors.ownerId = '$userID', sectors.captureTime = now(), sectors.level = '1' WHERE sectors.sectorId = '$sectorID';";
			mysqli_query($conn, $sql);
			mysqli_query($conn, $sqlActionLog); //1.0.1
			if ($ownerID != '1') { //Pridedam goldo uz nugalejima tik tada kai sektorius buvo kazkieno o ne "god"
				$sql = "UPDATE users SET users.gold = users.gold + '$recievedGold' WHERE users.id = '$userID';";
				mysqli_query($conn, $sql);
				if($sectorLevel == '1')//Nerasom gavote +0 aukso kai uzima lvl 1 sektoriu, nes dumb atrodo
				{
					$message = "Sėkmingai nugalėjote priešą ir užėmėte jo teritoriją.";
				}
				else
				{
					$message = "Sėkmingai nugalėjote priešą ir užėmėte jo teritoriją bei gavote +$recievedGold aukso.";
				}
			} else {
				$message = "";
			}
			$code = "success";
			array_push($response, array("code"=>$code, "message"=>$message));
			echo json_encode($response);
		} else { //Nepraejo valanda, nieko nedaro m
			$code = "fail";
			$message = "Nepavyko užimti, teritorijai galioja apsauga.";
			array_push($response, array("code"=>$code, "message"=>$message));
			echo json_encode($response);
		}
	} else { //Owneris viduje
		if ($hourdiff < 6) { //Owneris viduje ir aktyvus
			$code = "fail";
			$message = "Nepavyko užimti, nes riteris yra savo teritorijoje.";
			array_push($response, array("code"=>$code, "message"=>$message));
			echo json_encode($response);
		} else { //Owneris viduje bet neaktyvus
			if ($difference > 1) { //Jei praejo daugiau nei valanda nuo lastCapture
			$recievedGold = ($sectorLevel-1)*30; //1.1.0
			$sql = "UPDATE sectors SET sectors.ownerId = '$userID', sectors.captureTime = now(), sectors.level = '1' WHERE sectors.sectorId = '$sectorID';";
			mysqli_query($conn, $sql);
			mysqli_query($conn, $sqlActionLog); //1.0.1
			if ($ownerID != '1') { //Pridedam goldo uz nugalejima tik tada kai sektorius buvo kazkieno o ne "god"
				$sql = "UPDATE users SET users.gold = users.gold + '$recievedGold' WHERE users.id = '$userID';";
				mysqli_query($conn, $sql);
				if($sectorLevel == '1') //Nerasom gavote +0 aukso kai uzima lvl 1 sektoriu, nes dumb atrodo
				{
					$message = "Sėkmingai nugalėjote priešą ir užėmėte jo teritoriją.";
				}
				else
				{
					$message = "Sėkmingai nugalėjote priešą ir užėmėte jo teritoriją bei gavote +$recievedGold aukso.";
				}
			} else {
				$message = "";
			}
			$code = "success";
			array_push($response, array("code"=>$code, "message"=>$message));
			echo json_encode($response);
			} else { //Nepraejo valanda, nieko nedaro m
				$code = "fail";
				$message = "Nepavyko užimti, teritorijai galioja apsauga.";
				array_push($response, array("code"=>$code, "message"=>$message));
				echo json_encode($response);
			}
		}
	}
} else { //If query failed
	$code = "fail";
	$message = "Query failed";
	array_push($response, array("code"=>$code, "message"=>$message));
	echo json_encode($response);
}
?>