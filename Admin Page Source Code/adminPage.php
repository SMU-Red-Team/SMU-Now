<?php
include('session.php');

if(isset($_GET['Message']))
{
	print '<script type="text/javascript">alert("' . $_GET['Message'] . '");</script>';
}
   
?>
<!DOCTYPE html>
<html>
<head>
<title>Admin Page</title>
<link href="style.css" rel="stylesheet" type="text/css">
</head>
<body>
<div id="main">
	<h1 id="welcome">Welcome to the Admin Page: <i><?php echo $login_session; ?></i></h1>
	<h2 id="logout"><a href="logout.php">Log Out</a></h2>

	<div id="addUser" class="divStyle">
		<h3> Add User </h3>
		<form action="adminApi.php" method="post">
			Username: <input type="text" name="username"><br>
			Password: <input type="password" name="password"><br>
			Is Admin (Y/N): <input type="text" name="isAdmin"><br>
			<input type="submit" name="addUser" value="Add User">
		</form>
	</div>

	<div class="divStyle">
		<h3> Remove User</h3>
		<form action="adminApi.php" method="post">
			Username: <input type="text" name="username"><br>
			<input type="submit" name="removeUser" value="Remove User">
		</form>
	</div>

	<!--<div class="divStyle"> 
		<h3> Add Event </h3>
		<form action="adminApi.php" method="post">
			Event Name: <input type="text" name="eventName"><br>
			Description: <input type="text" name="description"><br>
			Start Date: <input type="text" name="startDate"><br>
			Location: <input type="text" name="location"><br>
			<input type="submit" name="addEvent" value="Add Event">
		</form>
	</div> -->

	<div class="divStyle">
		<h3> Delete Event </h3>
		<form action="adminApi.php" method="post">
			Event Name: <input type="text" name="eventName"><br>
			Event ID: <input type="text" name="eventID"><br>
			<input type="submit" name="removeEvent" value="Delete Event">
		</form>
	</div>
</div>

</body>
</html>
