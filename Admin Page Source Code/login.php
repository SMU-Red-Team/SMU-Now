<?php
session_start(); // Starting Session
$error=''; // Variable To Store Error Message
if (isset($_POST['submit'])) {
if (empty($_POST['username']) || empty($_POST['password'])) {
$error = "Username or Password is invalid";
}
else
{
// Define $username and $password
$username=$_POST['username'];
$password=$_POST['password'];

// Establishing Connection with Server by passing server_name, user_id and password as a parameter
$connection = mysql_connect("localhost", "root", "SMUNow1");
if(!$connection)
{
	die('Could not connect: ' . mysql_error());
}

// To protect MySQL injection for Security purpose
$username = stripslashes($username);
$password = stripslashes($password);
$username = mysql_real_escape_string($username);
$password = mysql_real_escape_string($password);
// Selecting Database
$db = mysql_select_db("SMUNow", $connection);
// SQL query to fetch information of registerd users and finds user match.
$query = "select * from Users where pass='" . $password . "' AND username='" . $username . "' AND isAdmin='1'";
//need to add isAdming

$result = mysql_query($query);

$rows = mysql_num_rows($result);

if ($rows == 1) {
$_SESSION['login_user']=$username; // Initializing Session
header("location: adminPage.php"); // Redirecting To Other Page
} else {
$error = "Username or Password is invalid";
}
mysql_close($connection); // Closing Connection
}
}
?>
