<?php

//connect to database
$con = mysql_connect("localhost", "root", "SMUNow1");
if(!$con)
{
	die('Could not connect: ' . mysql_error());
}

mysql_select_db("SMUNow", $con) 
	or die("Unable to select database:" . mysql_error());

//set today's date
$today = getdate();
$date = $today[mon] . "/" . $today[mday] . "/" . $today[year];

$param = $_POST['request'];

if(strcmp($param, "get events") == 0) //return all events
{
	$query = "SELECT * FROM Events join Contacts on Events.contactID=Contacts.contactID
			  join Location on Events.locationID=Location.locationID
			  WHERE Events.startDate>='$date'
			  ORDER BY Events.startDate ASC, STR_TO_DATE(Events.startTime, '%I:%i %p') ASC";

	$result = mysql_query($query);
	encode($result);

}
elseif(strcmp($param, "saved events") == 0) //return all saved events for user
{
	//should add a check to see if set...
	$userID = $_POST['userID'];

	$query = "SELECT * FROM Events join SavedEvents on SavedEvents.eventID=Events.eventID
				join Contacts on Events.contactID=Contacts.contactID
				join Location on Events.locationID=Location.locationID
				WHERE SavedEvents.userID = '$userID' 
				AND Events.startDate>='$date'
				ORDER BY Events.startDate ASC, STR_TO_DATE(Events.startTime, '%I:%i %p') ASC";

	$result = mysql_query($query);
	encode($result);
}
elseif(strcmp($param, "user events") == 0) //returns "not deleted" events
{
	//should add a check to see if set...
	$userID = $_POST['userID'];

	$query = "SELECT * FROM Events e join Contacts on e.contactID=Contacts.contactID
				join Location on e.locationID=Location.locationID
			  	WHERE NOT EXISTS (SELECT d.eventID FROM DeletedEvents d
			  						WHERE e.eventID = d.eventID
			  						AND d.userID='$userID')
				AND NOT EXISTS (SELECT s.eventID FROM SavedEvents s
									WHERE e.eventID = s.eventID
									AND s.userID='$userID')
				AND e.startDate>='$date'
				ORDER BY e.startDate ASC, STR_TO_DATE(e.startTime, '%I:%i %p') ASC";
	
	$result = mysql_query($query);
	encode($result);
}
elseif(strcmp($param, "register") == 0) //register a new user
{
	//should add a check to see if set...
	$email = $_POST['email'];
	$pass = $_POST['password'];
	$salt = "40";

	$query = "SELECT * FROM Users WHERE username='$email'";
	$result = mysql_query($query);

	$rows = mysql_num_rows($result);
	if($rows >= 1){
		echo "null";
	}
	else{
		$query = "INSERT INTO Users (username, pass, salt, isAdmin)
					Values ('$email', '$pass', '$salt', 0)";
		$result = mysql_query($query);
		
		$query2 = "SELECT userID FROM Users WHERE username='$email' AND pass='$pass'";
		$result2 = mysql_query($query2);
		encode($result2);
	}
}
elseif(strcmp($param, "login") == 0) //login existing user
{
	//should add a check to see if set...
	$email = $_POST['email'];
	$pass = $_POST['password'];

	$query = "SELECT userID FROM Users WHERE username='$email' AND pass='$pass'";
	$result = mysql_query($query);

	//if the Select statement == 1, then user exists
	$rows = mysql_num_rows($result);
	if ($rows == 1) {
		encode ($result);
	}
	else{
		echo "null"; //user does not exist in database
	}
}
elseif(strcmp($param, "delete saved event") == 0) //delete event from SavedEvents table
{
	//should add a check to see if set...
	$userID = $_POST['userID'];
	$eventID = $_POST['eventID'];

	$query = "DELETE FROM SavedEvents WHERE userID='$userID' AND eventID='$eventID'";
	$result = mysql_query($query);
}
elseif(strcmp($param, "delete event") == 0) //delete event from main view
{
	$userID = $_POST['userID'];
	$eventID = $_POST['eventID'];

	$query = "INSERT INTO DeletedEvents (userID, eventID) VALUES ('$userID', '$eventID')";

	$result = mysql_query($query);

	echo "successful";
}
elseif(strcmp($param, "save event") == 0) //save event of logged in user
{
	//should add a check to see if set...
	$userID = $_POST['userID'];
	$eventID = $_POST['eventID'];

	$query = "INSERT INTO SavedEvents (userID, eventID) 
				VALUES ('$userID', '$eventID')";
	$result = mysql_query($query);
}
else 
{
	echo "param not valid";
}

function encode($result)
{
	if(!$result)
	{
		$message = 'Database Select failed: ' . mysql_error() . "\n";
	    $message .= 'Whole query: ' . $query;
	    die($message);
	}

	$rows = array();
	while($r = mysql_fetch_assoc($result)) 
	{
	    $rows[] = $r;
	}
	echo json_encode($rows);
}

mysql_close($con);
?>
