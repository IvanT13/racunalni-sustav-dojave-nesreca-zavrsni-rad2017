<?php
	if($_SERVER["REQUEST_METHOD"]=="POST"){
		
		require 'connection.php';
		stvoriDojavu();
		
	}


	function stvoriDojavu(){
		
		global $connect;
		
		$vrijeme_dojave = date("Y-m-d H:i:s");
		$ime = $_POST["ime"];
		$prezime = $_POST["prezime"];
		$dat_rod = $_POST["dat_rod"];
		$krvna_grupa = $_POST["kg"];
		$mobitel = $_POST["mobitel"];
		$mobitelICE = $_POST["mobitelICE"];
		$vrsta_nesrece = $_POST["vrsta_nesrece"];
		$podvrsta_nesrece = $_POST["podvrsta_nesrece"];
		$ozlijedeni = $_POST["ozlijedeni"];
		$vozila = $_POST["vozila"];
		$gps_duzina = $_POST["gps_duzina"];
		$gps_sirina = $_POST["gps_sirina"];
		$foto = $_POST["foto"];
		
		$query = "INSERT INTO dojave(dojava_primljena,ime,prezime,dat_rod,krvna_grupa,mobitel,mobitel_ICE,vrsta_nesrece,podvrsta_nesrece,ozlijedeni,vozila,gps_duzina,gps_sirina,foto) 
		VALUES ('$vrijeme_dojave','$ime','$prezime','$dat_rod','$krvna_grupa','$mobitel','$mobitelICE','$vrsta_nesrece','$podvrsta_nesrece','$ozlijedeni','$vozila','$gps_duzina','$gps_sirina','$foto');";
	
		mysqli_query($connect,$query) or die(mysqli_error($connect));
		
		mysqli_close($connect);
		
	}

?>
	
	