<?php
require "conn.php";
$sectorCount = 100;
$n = 0;
while ($n < $sectorCount) {
	$n = $n + 1;

	$sql = "insert into urbanknights.sectors (sectorId, ownerId, level, captureTime) values ('$n', 0, 0, 0)";
	$qry = mysqli_query($conn, $sql);
}
?>