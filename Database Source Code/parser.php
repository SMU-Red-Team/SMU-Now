<?php
	//display errors
	ini_set('display_errors',1);
	ini_set('display_startup_errors',1);
	error_reporting(-1);
	
	//connect to smu now database
	$sqlconnection = mysqli_connect("localhost", "root", "SMUNow1", "SMUNow");
	
	//if connection fails end the program
	if(mysqli_connect_errno()) {
		echo "failed to connect".mysqli_connect_error();
		exit();
	}

	//load in events xml file into xml object
	$events = simplexml_load_file('events.xml');

	//check that the events xml was loaded properly
	if($events == false){
		echo "failed";
		exit();
	} else {
		echo "success";
	}

	$isfirst = 1;

	foreach($events as $event){

		//skip first line of file
		if($isfirst == 1){
			$isfirst = 0;
			continue;
		}

		//initialize variables
		$contactID = 0;
		$allDay = 0;
		$isSeries = 0;
		$locationID = 0;

		//strip addictional tags from data
		$event->EventName = strip_tags($event->EventName);
		$event->EventDescription = strip_tags($event->EventDescription);
		$event->ContactName = strip_tags($event->ContactName);
		$event->ContactPhone = strip_tags($event->ContactPhone);
		$event->ContactEmail = strip_tags($event->ContactEmail);
		$event->Categorization = strip_tags($event->Categorization);
		$event->Location = strip_tags($event->Location);
		$event->Building = strip_tags($event->Building);
		$event->Room = strip_tags($event->Room);
		$event->RoomDivision = strip_tags($event->RoomDivision);
		$event->RoomLink = strip_tags($event->RoomLink);
		$event->ExternalField1 = strip_tags($event->ExternalField1);
		$event->ExternalField2 = strip_tags($event->ExternalField2);
		$event->ExternalField3 = strip_tags($event->ExternalField3);
		$event->ExternalField4 = strip_tags($event->ExternalField4);
		$event->StartDate = strip_tags($event->StartDate);
		$event->StartTime = strip_tags($event->StartTime);
		$event->EndDate = strip_tags($event->EndDate);
		$event->EndTime = strip_tags($event->EndTime);
		$event->Status = strip_tags($event->Status);
		$event->Address1 = strip_tags($event->Address1);
		$event->Address2 = strip_tags($event->Address2);
		$event->City = strip_tags($event->City);
		$event->State = strip_tags($event->State);
		$event->Zipcode = strip_tags($event->Zipcode);
		$event->LocationUrl = strip_tags($event->LocationUrl);

		//escape all special characters from data
		$event->EventName = mysqli_real_escape_string($sqlconnection, $event->EventName);
		$event->EventDescription = mysqli_real_escape_string($sqlconnection, $event->EventDescription);
		$event->ContactName = mysqli_real_escape_string($sqlconnection, $event->ContactName);
		$event->ContactPhone = mysqli_real_escape_string($sqlconnection, $event->ContactPhone);
		$event->ContactEmail = mysqli_real_escape_string($sqlconnection, $event->ContactEmail);
		$event->Categorization = mysqli_real_escape_string($sqlconnection, $event->Categorization);
		$event->Location = mysqli_real_escape_string($sqlconnection, $event->Location);
		$event->Building = mysqli_real_escape_string($sqlconnection, $event->Building);
		$event->Room = mysqli_real_escape_string($sqlconnection, $event->Room);
		$event->RoomDivision = mysqli_real_escape_string($sqlconnection, $event->RoomDivision);
		$event->RoomLink = mysqli_real_escape_string($sqlconnection, $event->RoomLink);
		$event->ExternalField1 = mysqli_real_escape_string($sqlconnection, $event->ExternalField1);
		$event->ExternalField2 = mysqli_real_escape_string($sqlconnection, $event->ExternalField2);
		$event->ExternalField3 = mysqli_real_escape_string($sqlconnection, $event->ExternalField3);
		$event->ExternalField4 = mysqli_real_escape_string($sqlconnection, $event->ExternalField4);
		$event->StartDate = mysqli_real_escape_string($sqlconnection, $event->StartDate);
		$event->StartTime = mysqli_real_escape_string($sqlconnection, $event->StartTime);
		$event->EndDate = mysqli_real_escape_string($sqlconnection, $event->EndDate);
		$event->EndTime = mysqli_real_escape_string($sqlconnection, $event->EndTime);
		$event->Status = mysqli_real_escape_string($sqlconnection, $event->Status);
		$event->Address1 = mysqli_real_escape_string($sqlconnection, $event->Address1);
		$event->Address2 = mysqli_real_escape_string($sqlconnection, $event->Address2);
		$event->City = mysqli_real_escape_string($sqlconnection, $event->City);
		$event->State = mysqli_real_escape_string($sqlconnection, $event->State);
		$event->Zipcode = mysqli_real_escape_string($sqlconnection, $event->Zipcode);
		$event->LocationUrl = mysqli_real_escape_string($sqlconnection, $event->LocationUrl);

		$contactPhone = preg_replace("/[^0-9]/", "", $event->ContactPhone);

		//check if contact already exists
		$query = "Select * FROM Contacts WHERE phone=".$contactPhone." and email='".$event->ContactEmail."'";
		$result = mysqli_query($sqlconnection, $query);

		if ($result->num_rows == 0) {
			$query = "INSERT INTO Contacts(phone, email) VALUES(".$contactPhone.", '".$event->ContactEmail."')";
			$result = mysqli_query($sqlconnection, $query);

			$query = "SELECT MAX(contactID) FROM Contacts";
			$result = mysqli_query($sqlconnection, $query);

			$row = $result->fetch_assoc();
			$contactID = $row['MAX(contactID)'];
		} else {
			$row = $result->fetch_assoc();
			$contactID = $row['contactID'];
		}


		//check if the location already exists
		$query = "Select * FROM Location WHERE building='".$event->Building."' and room='".$event->Room."' and roomDiv='".$event->RoomDivision."' and roomLink='".$event->RoomLink."'";
		$result = mysqli_query($sqlconnection, $query);

		if ($result->num_rows == 0) {
			$query = "INSERT INTO Location(building, room, roomDiv, RoomLink) VALUES('".$event->Building."', '".$event->Room."', '".$event->RoomDivision."', '".$event->RoomLink."')";
			$result = mysqli_query($sqlconnection, $query);

			$query = "SELECT MAX(locationID) FROM Location";
			$result = mysqli_query($sqlconnection, $query);

			$row = $result->fetch_assoc();
			$locationID = $row['MAX(locationID)'];
		} else {
			$row = $result->fetch_assoc();
			$locationID = $row['locationID'];
		}

		if($event->AllDayFlag == "Y"){
			$allDay = 1;
		} else {
			$allDay = 0;
		}

		if($event->OneTimeSeries == "Series"){
			$isSeries = 1;
		} else {
			$isSeries = 0;
		}

		//insert event into database
		$query = "INSERT INTO Events(
			name,
			description,
			contactID,
			externalField1,
			externalField2,
			externalField3,
			externalField4,
			allDay,
			startDate,
			endDate,
			isSeries,
			status,
			address1,
			address2,
			city,
			state,
			zip,
			url,
			categorization,
			locationID,
			startTime,
			endTime
			) Values (
			'".$event->EventName."',
			'".$event->EventDescription."',
			".$contactID.",
			'".$event->ExternalField1."',
			'".$event->ExternalField2."',
			'".$event->ExternalField3."',
			'".$event->ExternalField4."',
			".$allDay.",
			'".$event->StartDate."',
			'".$event->EndDate."',
			".$isSeries.",
			'".$event->Status."',
			'".$event->Address1."',
			'".$event->Address2."',
			'".$event->City."',
			'".$event->State."',
			'".$event->Zipcode."',
			'".$event->LocationUrl."',
			'".$event->Categorization."',
			".$locationID.",
			'".$event->StartTime."',
			'".$event->EndTime."')";

		$result = mysqli_query($sqlconnection, $query)  or die(mysqli_error($sqlconnection));
		

	}
?>
