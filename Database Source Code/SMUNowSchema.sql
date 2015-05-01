-- MySQL dump 10.13  Distrib 5.5.41, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: SMUNow
-- ------------------------------------------------------
-- Server version	5.5.41-0ubuntu0.14.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Contacts`
--

DROP TABLE IF EXISTS `Contacts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Contacts` (
  `contactID` int(11) NOT NULL AUTO_INCREMENT,
  `phone` varchar(15) DEFAULT NULL,
  `email` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`contactID`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `DeletedEvents`
--

DROP TABLE IF EXISTS `DeletedEvents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `DeletedEvents` (
  `userID` int(11) NOT NULL,
  `eventID` int(11) NOT NULL,
  PRIMARY KEY (`userID`,`eventID`),
  KEY `eventID` (`eventID`),
  CONSTRAINT `DeletedEvents_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `Users` (`userID`),
  CONSTRAINT `DeletedEvents_ibfk_2` FOREIGN KEY (`eventID`) REFERENCES `Events` (`eventID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Events`
--

DROP TABLE IF EXISTS `Events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Events` (
  `eventID` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(512) NOT NULL,
  `description` varchar(2048) NOT NULL,
  `contactID` int(11) NOT NULL,
  `externalField1` varchar(512) DEFAULT NULL,
  `externalField2` varchar(512) DEFAULT NULL,
  `externalField3` varchar(512) DEFAULT NULL,
  `externalField4` varchar(512) DEFAULT NULL,
  `allDay` tinyint(1) DEFAULT NULL,
  `startDate` varchar(256) DEFAULT NULL,
  `endDate` varchar(256) DEFAULT NULL,
  `isSeries` tinyint(1) DEFAULT NULL,
  `status` varchar(512) DEFAULT NULL,
  `address1` varchar(512) DEFAULT NULL,
  `address2` varchar(512) DEFAULT NULL,
  `city` varchar(256) DEFAULT NULL,
  `state` varchar(256) DEFAULT NULL,
  `zip` varchar(256) DEFAULT NULL,
  `url` varchar(512) DEFAULT NULL,
  `categorization` varchar(512) DEFAULT NULL,
  `locationID` int(11) DEFAULT NULL,
  `startTime` varchar(256) DEFAULT NULL,
  `endTime` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`eventID`),
  KEY `locationID` (`locationID`),
  KEY `contactID` (`contactID`),
  CONSTRAINT `Events_ibfk_1` FOREIGN KEY (`locationID`) REFERENCES `Location` (`locationID`),
  CONSTRAINT `Events_ibfk_2` FOREIGN KEY (`contactID`) REFERENCES `Contacts` (`contactID`)
) ENGINE=InnoDB AUTO_INCREMENT=258 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Location`
--

DROP TABLE IF EXISTS `Location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Location` (
  `locationID` int(11) NOT NULL AUTO_INCREMENT,
  `building` varchar(256) DEFAULT NULL,
  `room` varchar(256) DEFAULT NULL,
  `roomDiv` varchar(256) DEFAULT NULL,
  `roomLink` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`locationID`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SavedEvents`
--

DROP TABLE IF EXISTS `SavedEvents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SavedEvents` (
  `userID` int(11) NOT NULL,
  `eventID` int(11) NOT NULL,
  PRIMARY KEY (`userID`,`eventID`),
  KEY `eventID` (`eventID`),
  CONSTRAINT `SavedEvents_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `Users` (`userID`),
  CONSTRAINT `SavedEvents_ibfk_2` FOREIGN KEY (`eventID`) REFERENCES `Events` (`eventID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Users`
--

DROP TABLE IF EXISTS `Users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Users` (
  `userID` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(256) NOT NULL,
  `pass` varchar(512) NOT NULL,
  `salt` varchar(512) NOT NULL,
  `isAdmin` tinyint(1) NOT NULL,
  PRIMARY KEY (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-04-30  4:06:02
