-- MySQL dump 10.13  Distrib 5.7.18, for macos10.12 (x86_64)
--
-- Host: localhost    Database: andonsys
-- ------------------------------------------------------
-- Server version	5.7.18

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
-- Current Database: `andonsys`
--

/*!40000 DROP DATABASE IF EXISTS `andonsys`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `andonsys` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `andonsys`;

--
-- Table structure for table `buyer`
--

DROP TABLE IF EXISTS `buyer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `buyer` (
  `buyer_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `team` varchar(255) NOT NULL,
  PRIMARY KEY (`buyer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `buyer`
--

LOCK TABLES `buyer` WRITE;
/*!40000 ALTER TABLE `buyer` DISABLE KEYS */;
/*!40000 ALTER TABLE `buyer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `designation`
--

DROP TABLE IF EXISTS `designation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `designation` (
  `desgn_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `level` int(11) DEFAULT NULL,
  `line` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`desgn_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `designation`
--

LOCK TABLES `designation` WRITE;
/*!40000 ALTER TABLE `designation` DISABLE KEYS */;
/*!40000 ALTER TABLE `designation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `designation_problem`
--

DROP TABLE IF EXISTS `designation_problem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `designation_problem` (
  `desgn_id` bigint(20) NOT NULL,
  `prob_id` bigint(20) NOT NULL,
  PRIMARY KEY (`desgn_id`,`prob_id`),
  KEY `FKcexng161aylm6t45rvrrqxrx8` (`prob_id`),
  CONSTRAINT `FK2lyv1emobe1xx2mf2bucyegqb` FOREIGN KEY (`desgn_id`) REFERENCES `designation` (`desgn_id`),
  CONSTRAINT `FKcexng161aylm6t45rvrrqxrx8` FOREIGN KEY (`prob_id`) REFERENCES `problem` (`prob_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `designation_problem`
--

LOCK TABLES `designation_problem` WRITE;
/*!40000 ALTER TABLE `designation_problem` DISABLE KEYS */;
/*!40000 ALTER TABLE `designation_problem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `issue1`
--

DROP TABLE IF EXISTS `issue1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `issue1` (
  `issue_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ack_at` datetime DEFAULT NULL,
  `critical` varchar(3) DEFAULT NULL,
  `deleted` bit(1) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `fix_at` datetime DEFAULT NULL,
  `last_modified` datetime DEFAULT NULL,
  `line` int(11) NOT NULL,
  `operator_no` varchar(10) DEFAULT NULL,
  `processing_at` int(11) DEFAULT NULL,
  `raised_at` datetime DEFAULT NULL,
  `section` varchar(255) NOT NULL,
  `seek_help` int(11) DEFAULT NULL,
  `ack_by` bigint(20) DEFAULT NULL,
  `fix_by` bigint(20) DEFAULT NULL,
  `prob_id` bigint(20) DEFAULT NULL,
  `raised_by` bigint(20) NOT NULL,
  PRIMARY KEY (`issue_id`),
  KEY `FK6r3aqg747v1uwk3ce8afeb0m7` (`ack_by`),
  KEY `FKiasa74eub4t52pefp8v1dfueo` (`fix_by`),
  KEY `FKb136mu0bff0o1qct68fuc396y` (`prob_id`),
  KEY `FKnn7360buor5h7nhbc3crdp1dp` (`raised_by`),
  CONSTRAINT `FK6r3aqg747v1uwk3ce8afeb0m7` FOREIGN KEY (`ack_by`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FKb136mu0bff0o1qct68fuc396y` FOREIGN KEY (`prob_id`) REFERENCES `problem` (`prob_id`),
  CONSTRAINT `FKiasa74eub4t52pefp8v1dfueo` FOREIGN KEY (`fix_by`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FKnn7360buor5h7nhbc3crdp1dp` FOREIGN KEY (`raised_by`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `issue1`
--

LOCK TABLES `issue1` WRITE;
/*!40000 ALTER TABLE `issue1` DISABLE KEYS */;
/*!40000 ALTER TABLE `issue1` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `issue2`
--

DROP TABLE IF EXISTS `issue2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `issue2` (
  `issue_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ack_at` datetime DEFAULT NULL,
  `deleted` bit(1) NOT NULL,
  `description` varchar(255) NOT NULL,
  `fix_at` datetime DEFAULT NULL,
  `last_modified` datetime DEFAULT NULL,
  `problem` varchar(255) NOT NULL,
  `processing_at` int(11) DEFAULT NULL,
  `raised_at` datetime NOT NULL,
  `ack_by` bigint(20) DEFAULT NULL,
  `buyer_id` bigint(20) NOT NULL,
  `fix_by` bigint(20) DEFAULT NULL,
  `raised_by` bigint(20) NOT NULL,
  PRIMARY KEY (`issue_id`),
  KEY `FKq08pk1osq38c8x2hx4mggo64a` (`ack_by`),
  KEY `FK639mpps1jvsn7nyoy7fwnibth` (`buyer_id`),
  KEY `FKtelmm9nvydaohfsc86qkrh63h` (`fix_by`),
  KEY `FKe3injn0v6fk3ohgm12do0q033` (`raised_by`),
  CONSTRAINT `FK639mpps1jvsn7nyoy7fwnibth` FOREIGN KEY (`buyer_id`) REFERENCES `buyer` (`buyer_id`),
  CONSTRAINT `FKe3injn0v6fk3ohgm12do0q033` FOREIGN KEY (`raised_by`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FKq08pk1osq38c8x2hx4mggo64a` FOREIGN KEY (`ack_by`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FKtelmm9nvydaohfsc86qkrh63h` FOREIGN KEY (`fix_by`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `issue2`
--

LOCK TABLES `issue2` WRITE;
/*!40000 ALTER TABLE `issue2` DISABLE KEYS */;
/*!40000 ALTER TABLE `issue2` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `problem`
--

DROP TABLE IF EXISTS `problem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `problem` (
  `prob_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `department` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`prob_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `problem`
--

LOCK TABLES `problem` WRITE;
/*!40000 ALTER TABLE `problem` DISABLE KEYS */;
/*!40000 ALTER TABLE `problem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `last_modified` datetime DEFAULT NULL,
  `level` varchar(255) DEFAULT NULL,
  `mobile` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `otp` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `retry_count` int(11) DEFAULT NULL,
  `role` varchar(255) NOT NULL,
  `user_type` varchar(255) NOT NULL,
  `desgn_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK_ob8kqyqqgmefl0aco34akdtpe` (`email`),
  KEY `FKb7tlx120ff5lrclg540kbeeo0` (`desgn_id`),
  CONSTRAINT `FKb7tlx120ff5lrclg540kbeeo0` FOREIGN KEY (`desgn_id`) REFERENCES `designation` (`desgn_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'','jawed.akhtar1993@gmail.com','2017-06-17 18:13:17','LEVEL4','8987525008','Md Jawed Akhtar',NULL,'$2a$10$OUFYPJNblUyb2sUEAdPj4.2ADa7uRQIRXu.MbQ1nHRd7LSaF0HXxy',NULL,'ADMIN','MERCHANDISING',NULL),(2,'','zahid7292@gmail.com','2017-06-17 18:13:17','LEVEL4','8987525008','Md Zahid Raza',NULL,'$2a$10$.d8fm1/Wr9EKGOyfDHP0FesZmK1E3d.C9g9YwuhSG8UT66q.frdqO',NULL,'ADMIN','SAMPLING',NULL),(3,'','taufeeque8@gmail.com','2017-06-17 18:13:17','LEVEL4','8987525008','Md Taufeeque Alam',NULL,'$2a$10$JO.meetW6Bj3AlQpYmPBrubc07w0fBqgSwwVjB8haZw3Ii.5c/uq.',NULL,'ADMIN','FACTORY',NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_buyer`
--

DROP TABLE IF EXISTS `user_buyer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_buyer` (
  `user_id` bigint(20) NOT NULL,
  `buyer_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`buyer_id`),
  KEY `FK8qg2qgjgwvapwjl5v0jcs48qo` (`buyer_id`),
  CONSTRAINT `FK8qg2qgjgwvapwjl5v0jcs48qo` FOREIGN KEY (`buyer_id`) REFERENCES `buyer` (`buyer_id`),
  CONSTRAINT `FKah2vub63uxyh838m026hsixg0` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_buyer`
--

LOCK TABLES `user_buyer` WRITE;
/*!40000 ALTER TABLE `user_buyer` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_buyer` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-06-17 18:15:22
