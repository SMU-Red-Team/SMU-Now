<?php
include('login.php'); // Includes Login Script

if(isset($_SESSION['login_user'])){
header("location: adminPage.php");
}
?>
<!DOCTYPE html>
<html>
<head>
<title>SMUNow Admin Login</title>
<link href="style.css" rel="stylesheet" type="text/css">
</head>
<body>
<div id="main">
<h1 id="SMUNow">SMUNow Admin Login</h1>
<div id="login">
<form id = "loginForm" action="" method="post">
	<label>UserName :</label>
	<input class="loginBox" id="name" name="username" placeholder="username" type="text">
	<label>Password :</label>
	<input class="loginBox" id="password" name="password" placeholder="**********" type="password">
	<input name="submit" type="submit" value=" Login ">
	<span><?php echo $error; ?></span>
</form>
</div>
</div>
</body>
</html>
