<html>
	<head>
		<meta charset="utf-8"/>
		<title>Dojave 112</title>
		<link rel="stylesheet" href="dojave_style.css">
		
		<style>
       #map {
        height: 80%;
        width: 100%;
       }
    </style>
	
	<script>
		function button_nazad(){
			
			//window.open("prikazDojave.php","_self");
			history.go(-1);
		
		}
	
	</script>

	</head>
	<body>
	<div id="wrapper">
		<div id='info'>
		<p class="naslov"> INFORMACIJE O DOJAVI </p>
		
		<?php
			
			require 'connection.php';
			ispis();
			
			function ispis(){
				
				global $connect;
				$id = $_GET['id'];
				$query = "SELECT * FROM dojave WHERE ID = $id";
				$sqli = mysqli_query($connect,$query);
				
				while($record = mysqli_fetch_array($sqli)){
					
					echo "<p class='informacije'>Dojava primljena: " . $record['dojava_primljena'] . "</p>";
					echo "<p class='informacije'>Ime: " . $record['ime'] . "</p>";
					echo "<p class='informacije'>Prezime " . $record['prezime'] . "</p>";
					echo "<p class='informacije'>Datum rođenja: " . $record['dat_rod'] . "</p>";
					echo "<p class='informacije'>Krvna grupa: " . $record['krvna_grupa'] . "</p>";
					echo "<p class='informacije'>Mobitel: " . $record['mobitel'] . "</p>";
					echo "<p class='informacije'>Mobitel ICE: " . $record['mobitel_ICE'] . "</p>";
					echo "<p class='informacije'>Vrsta nesreće: " . $record['vrsta_nesrece'] . "</p>";
					echo "<p class='informacije'>Podvrsta nesreće: " . $record['podvrsta_nesrece'] . "</p>";
					echo "<p class='informacije'>Ozlijeđeni: " . $record['ozlijedeni'] . "</p>";
					echo "<p class='informacije'>Vozila: " . $record['vozila'] . "</p>";
					
				}
			
			}
		?>
		</div>
		<div id="foto">
		<p class="naslov"> FOTOGRAFIJA DOJAVE </p>
		 <?php
				global $connect;
				$id = $_GET['id'];
				$query = "SELECT * FROM dojave WHERE ID = $id";
				$sqli = mysqli_query($connect,$query);
				$record = mysqli_fetch_array($sqli);
				
				$foto_strcmp = strcmp($record['foto'],"0.jpg");
				if($foto_strcmp != 0){
					echo "<img src='slikeDojave/".$record['foto']."'"."width=99% height=376px	>";
				}
				else{
					echo "<img src='slikeDojave/noFoto.jpg' width=99% height=376px>";
				}
				
			?>
		 </div>
		
		
		<div id="lokacija">
			<p class="naslov">LOKACIJA DOJAVE</p>
			
			<div id="map"></div>
					
			<script>
			  function initMap() {
				  bounds  = new google.maps.LatLngBounds();
				<?php
					global $connect;
					$id = $_GET['id'];
					$query = "SELECT * FROM dojave WHERE ID = $id";
					$sqli = mysqli_query($connect,$query);
					$record = mysqli_fetch_array($sqli);
					echo "var uluru = {lat:". $record['gps_sirina'].","."lng:".$record['gps_duzina']."};"
				
				?>
				 
				var map = new google.maps.Map(document.getElementById('map'), {
				  zoom: 4,
				  center: uluru
				});
				var marker = new google.maps.Marker({
				  position: uluru,
				  map: map
				});
				
				loc = new google.maps.LatLng(marker.position.lat(), marker.position.lng());
				bounds.extend(loc);
				
				map.fitBounds(bounds);       
				map.panToBounds(bounds);

				var listener = google.maps.event.addListener(map, "idle", function () {
				map.setZoom(18);
				google.maps.event.removeListener(listener);
				});
			  }
			</script>
			<script async defer
			src="https://maps.googleapis.com/maps/api/js?key=AIzaSyB5CFuH6WKbrG5jiqJ0XjUfR90yL9JVrwM&callback=initMap">
			 </script> 
		</div>
		
		<button id="nazad" onClick="button_nazad()"> NAZAD </button>
	</div>
  </body>
</html>


