<?php
//connect to database
$con = mysql_connect("localhost", "root", "SMUNow1");
if(!$con)
{
	die('Could not connect: ' . mysql_error());
}

mysql_select_db("SMUNow", $con) 
	or die("Unable to select database:" . mysql_error());

if(isset($_POST['addUser'])) //add a new user
{
	if(!empty($_POST['username']) || !empty($_POST['password']) || !empty($_POST['isAdmin'])) //don't run if any of the fields are empty
	{
		$username = $_POST['username'];
		$password = $_POST['password'];
		$temp = $_POST['isAdmin'];

		if(strcmp($temp, "Y") == 0 || strcmp($temp, "y")==0){ //set isAdmin to 1 or 0 based on the value
		$isAdmin = 1;
		}
		else{
			$isAdmin = 0;
		}
		
		$query = "SELECT * FROM Users WHERE username='$username'";
		$result = mysql_query($query);

		$rows = mysql_num_rows($result); //if result is >= 1, there is alread a user with that name
		if($rows >= 1){
			echo "null";
		}
		else
		{
			$query2 = "INSERT INTO Users (username,pass,salt,isAdmin)
					VALUES ('$username', '$password','20', '$isAdmin')";
			mysql_query($query2);
		
			$Message = "Please make sure all fields have been filled out";
			header('Location: /adminPage.php?Message=' .urlencode($Message)); //reload back to admin page with popup that says failure
		}	
	}

	$Message = "User added successflly";
	header('Location: /adminPage.php?Message=' .urlencode($Message)); //reload back to admin page with popup that says success
}
elseif(isset($_POST['removeUser'])) //remove user
{
	if(!empty($_POST['username'])) //field must be set
	{
		$username = $_POST['username'];

		$query = "DELETE FROM Users WHERE username='$username'";
		mysql_query($query);

		$Message = "User removed successfully";
		header('Location: /adminPage.php?Message=' .urlencode($Message)); 
	}
	else
	{
		$Message = "Please make sure field is filled out";
		header('Location: /adminPage.php?Message=' .urlencode($Message));	
	}

	
}
/*elseif(isset($_POST['addEvent'])) //Code to be used if developer wants to add events
{
	$eventName = $_POST['eventName'];
	$description = $_POST['descriptoin'];
	$startDate = $_POST['startDate'];
	$location = $_POST['location'];

	$query = "INSERT INTO Events (name,description,contactID,startDate, startTime, endTimelocationID) 
				VALUES ()";
	mysql_query($query);

	header('Location: /adminPage.php');
}*/
elseif(isset($_POST['removeEvent'])) //remove event
{
	if(!empty($_POST['eventName'])) //event name must be set
	{
		$eventName = $_POST['eventName'];
		$query = "DELETE FROM Events WHERE eventName='$eventName'";
		mysql_query($query);

		$Message = "Event deleted successfully";
		header('Location: /adminPage.php?Message=' .urlencode($Message));
	}
	elseif(!empty($_POST['eventID'])) //else eventID must be set
	{
		$eventID = $_POST['eventID'];
		$query = "DELETE FROM Events WHERE eventID='$eventID'";
		mysql_query($query);

		$Message = "Event deleted successfully";
		header('Location: /adminPage.php?Message=' .urlencode($Message));
	}
	else //else error
	{
		$Message = "Please make sure one of the fields is filled out";
		header('Location: /adminPage.php?Message=' .urlencode($Message));
	}
}

?>