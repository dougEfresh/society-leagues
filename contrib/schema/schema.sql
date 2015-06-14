-- MySQL dump 10.15  Distrib 10.0.17-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: 127.0.0.1    Database: leagues
-- ------------------------------------------------------
-- Server version	10.0.17-MariaDB-0ubuntu1

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
-- Table structure for table `challenge`
--

DROP TABLE IF EXISTS `challenge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `challenge` (
  `challenge_id` int(11) NOT NULL AUTO_INCREMENT,
  `player_challenger_id` int(11) NOT NULL,
  `player_opponent_id` int(11) NOT NULL,
  `slot_id` int(11) NOT NULL,
  `status` varchar(255) NOT NULL,
  `team_match_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`challenge_id`),
  KEY `player_opponent_id` (`player_opponent_id`),
  KEY `player_challenger_id` (`player_challenger_id`),
  KEY `slot_id` (`slot_id`),
  KEY `fk1` (`team_match_id`),
  CONSTRAINT `challenge_ibfk_1` FOREIGN KEY (`player_opponent_id`) REFERENCES `player` (`player_id`) ON DELETE CASCADE,
  CONSTRAINT `challenge_ibfk_2` FOREIGN KEY (`player_challenger_id`) REFERENCES `player` (`player_id`) ON DELETE CASCADE,
  CONSTRAINT `challenge_ibfk_3` FOREIGN KEY (`slot_id`) REFERENCES `slot` (`slot_id`) ON DELETE CASCADE,
  CONSTRAINT `fk1` FOREIGN KEY (`team_match_id`) REFERENCES `team_match` (`team_match_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=90000 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `division`
--

DROP TABLE IF EXISTS `division`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `division` (
  `division_id` int(11) NOT NULL AUTO_INCREMENT,
  `league_type` varchar(255) NOT NULL,
  `division_type` varchar(64) NOT NULL,
  PRIMARY KEY (`division_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5007 DEFAULT CHARSET=latin1;

insert into leagues.division(division_type,league_type)
VALUES ('EIGHT_BALL_WEDNESDAYS','TEAM');
insert into leagues.division(division_type,league_type)
VALUES ('EIGHT_BALL_THURSDAYS','TEAM');
insert into leagues.division(division_type,league_type)
VALUES ('NINE_BALL_TUESDAYS','TEAM');
insert into leagues.division(division_type,league_type)
VALUES ('EIGHT_BALL_CHALLENGE','INDIVIDUAL');
insert into leagues.division(division_type,league_type)
VALUES ('NINE_BALL_CHALLENGE','INDIVIDUAL');
insert into leagues.division(division_type,league_type)
VALUES ('EIGHT_BALL_MIXED_MONDAYS','TEAM');

insert into leagues.division(league_type,division_type) VALUES ('INDIVIDUAL','STRAIGHT'); 

/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `persistent_logins`
--

DROP TABLE IF EXISTS `persistent_logins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `persistent_logins` (
  `username` varchar(64) NOT NULL,
  `series` varchar(64) NOT NULL,
  `token` varchar(64) NOT NULL,
  `last_used` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`series`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `player`
--

DROP TABLE IF EXISTS `player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `player` (
  `player_id` int(11) NOT NULL AUTO_INCREMENT,
  `season_id` int(11) NOT NULL,
  `division_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `team_id` int(11) NOT NULL,
  `handicap` int(11) NOT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  PRIMARY KEY (`player_id`),
  KEY `division_id` (`division_id`),
  KEY `season_id` (`season_id`),
  KEY `user_id` (`user_id`),
  KEY `team_id` (`team_id`),
  CONSTRAINT `player_ibfk_1` FOREIGN KEY (`division_id`) REFERENCES `division` (`division_id`) ON DELETE CASCADE,
  CONSTRAINT `player_ibfk_2` FOREIGN KEY (`season_id`) REFERENCES `season` (`season_id`) ON DELETE CASCADE,
  CONSTRAINT `player_ibfk_3` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `player_ibfk_4` FOREIGN KEY (`team_id`) REFERENCES `team` (`team_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=61613 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary table structure for view `player_away_result_vw`
--

DROP TABLE IF EXISTS `player_away_result_vw`;
/*!50001 DROP VIEW IF EXISTS `player_away_result_vw`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `player_away_result_vw` (
  `player_away_id` tinyint NOT NULL,
  `user_id` tinyint NOT NULL,
  `win` tinyint NOT NULL,
  `lost` tinyint NOT NULL,
  `racks_for` tinyint NOT NULL,
  `racks_against` tinyint NOT NULL,
  `opponent_handicap` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `player_home_result_vw`
--

DROP TABLE IF EXISTS `player_home_result_vw`;
/*!50001 DROP VIEW IF EXISTS `player_home_result_vw`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `player_home_result_vw` (
  `player_home_id` tinyint NOT NULL,
  `user_id` tinyint NOT NULL,
  `win` tinyint NOT NULL,
  `lost` tinyint NOT NULL,
  `racks_for` tinyint NOT NULL,
  `racks_against` tinyint NOT NULL,
  `opponent_handicap` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `player_result`
--

DROP TABLE IF EXISTS `player_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `player_result` (
  `player_result_id` int(11) NOT NULL AUTO_INCREMENT,
  `team_match_id` int(11) NOT NULL,
  `player_home_id` int(11) NOT NULL,
  `player_away_id` int(11) NOT NULL,
  `home_racks` int(11) NOT NULL,
  `away_racks` int(11) NOT NULL,
  `match_number` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`player_result_id`),
  KEY `team_match_id` (`team_match_id`),
  KEY `player_home_id` (`player_home_id`),
  KEY `player_away_id` (`player_away_id`),
  KEY `home_pl` (`player_home_id`),
  KEY `away_pl` (`player_away_id`),
  CONSTRAINT `player_result_ibfk_1` FOREIGN KEY (`team_match_id`) REFERENCES `team_match` (`team_match_id`) ON DELETE CASCADE,
  CONSTRAINT `player_result_ibfk_2` FOREIGN KEY (`player_home_id`) REFERENCES `player` (`player_id`) ON DELETE CASCADE,
  CONSTRAINT `player_result_ibfk_3` FOREIGN KEY (`player_away_id`) REFERENCES `player` (`player_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=116279 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `season`
--

DROP TABLE IF EXISTS `season`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = latin1 */;
CREATE TABLE `season` (
  `season_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `end_date` date DEFAULT NULL,
  `updated` timestamp NULL DEFAULT NULL,
  `rounds` int(11) NOT NULL,
  `season_status` varchar(255) NOT NULL,
  `division_id` int(11) NOT NULL,
  `start_date` date DEFAULT NULL,
  PRIMARY KEY (`season_id`),
  KEY `division_id` (`division_id`),
  CONSTRAINT `season_ibfk_1` FOREIGN KEY (`division_id`) REFERENCES `division` (`division_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4002 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `slot`
--

DROP TABLE IF EXISTS `slot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `slot` (
  `slot_id` int(11) NOT NULL AUTO_INCREMENT,
  `slot_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `allocated` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`slot_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1249 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `team`
--

DROP TABLE IF EXISTS `team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `team` (
  `team_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9143 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary table structure for view `team_away_result_vw`
--

DROP TABLE IF EXISTS `team_away_result_vw`;
/*!50001 DROP VIEW IF EXISTS `team_away_result_vw`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `team_away_result_vw` (
  `team_id` tinyint NOT NULL,
  `season_id` tinyint NOT NULL,
  `win` tinyint NOT NULL,
  `lost` tinyint NOT NULL,
  `racks_for` tinyint NOT NULL,
  `racks_against` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `team_away_stats_vw`
--

DROP TABLE IF EXISTS `team_away_stats_vw`;
/*!50001 DROP VIEW IF EXISTS `team_away_stats_vw`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `team_away_stats_vw` (
  `team_id` tinyint NOT NULL,
  `season_id` tinyint NOT NULL,
  `wins` tinyint NOT NULL,
  `loses` tinyint NOT NULL,
  `racks_for` tinyint NOT NULL,
  `racks_against` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `team_home_result_vw`
--

DROP TABLE IF EXISTS `team_home_result_vw`;
/*!50001 DROP VIEW IF EXISTS `team_home_result_vw`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `team_home_result_vw` (
  `team_id` tinyint NOT NULL,
  `season_id` tinyint NOT NULL,
  `win` tinyint NOT NULL,
  `lost` tinyint NOT NULL,
  `racks_for` tinyint NOT NULL,
  `racks_against` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `team_home_stats_vw`
--

DROP TABLE IF EXISTS `team_home_stats_vw`;
/*!50001 DROP VIEW IF EXISTS `team_home_stats_vw`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `team_home_stats_vw` (
  `team_id` tinyint NOT NULL,
  `season_id` tinyint NOT NULL,
  `wins` tinyint NOT NULL,
  `loses` tinyint NOT NULL,
  `racks_for` tinyint NOT NULL,
  `racks_against` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `team_match`
--

DROP TABLE IF EXISTS `team_match`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `team_match` (
  `team_match_id` int(11) NOT NULL AUTO_INCREMENT,
  `season_id` int(11) NOT NULL,
  `home_team_id` int(11) NOT NULL,
  `away_team_id` int(11) NOT NULL,
  `match_date` timestamp NOT NULL,
  PRIMARY KEY (`team_match_id`),
  KEY `season_id` (`season_id`),
  KEY `home_team_id` (`home_team_id`),
  KEY `away_team_id` (`away_team_id`),
  CONSTRAINT `team_match_ibfk_1` FOREIGN KEY (`season_id`) REFERENCES `season` (`season_id`) ON DELETE CASCADE,
  CONSTRAINT `team_match_ibfk_2` FOREIGN KEY (`home_team_id`) REFERENCES `team` (`team_id`) ON DELETE CASCADE,
  CONSTRAINT `team_match_ibfk_3` FOREIGN KEY (`away_team_id`) REFERENCES `team` (`team_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21429 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `team_result`
--

DROP TABLE IF EXISTS `team_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `team_result` (
  `team_result_id` int(11) NOT NULL AUTO_INCREMENT,
  `team_match_id` int(11) NOT NULL,
  `home_racks` int(11) NOT NULL,
  `away_racks` int(11) NOT NULL,
  PRIMARY KEY (`team_result_id`),
  KEY `team_match_id` (`team_match_id`),
  CONSTRAINT `team_result_ibfk_1` FOREIGN KEY (`team_match_id`) REFERENCES `team_match` (`team_match_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21414 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary table structure for view `team_set_away_vw`
--

DROP TABLE IF EXISTS `team_set_away_vw`;
/*!50001 DROP VIEW IF EXISTS `team_set_away_vw`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `team_set_away_vw` (
  `team_id` tinyint NOT NULL,
  `season_id` tinyint NOT NULL,
  `setWins` tinyint NOT NULL,
  `setLoses` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `team_set_home_vw`
--

DROP TABLE IF EXISTS `team_set_home_vw`;
/*!50001 DROP VIEW IF EXISTS `team_set_home_vw`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `team_set_home_vw` (
  `team_id` tinyint NOT NULL,
  `season_id` tinyint NOT NULL,
  `setWins` tinyint NOT NULL,
  `setLoses` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `team_set_vw`
--

DROP TABLE IF EXISTS `team_set_vw`;
/*!50001 DROP VIEW IF EXISTS `team_set_vw`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `team_set_vw` (
  `team_id` tinyint NOT NULL,
  `season_id` tinyint NOT NULL,
  `setWins` tinyint NOT NULL,
  `setLoses` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `team_stats_vw`
--

DROP TABLE IF EXISTS `team_stats_vw`;
/*!50001 DROP VIEW IF EXISTS `team_stats_vw`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `team_stats_vw` (
  `team_id` tinyint NOT NULL,
  `season_id` tinyint NOT NULL,
  `wins` tinyint NOT NULL,
  `loses` tinyint NOT NULL,
  `racks_for` tinyint NOT NULL,
  `racks_against` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `user_stats_all_vw`
--

DROP TABLE IF EXISTS `user_stats_all_vw`;
/*!50001 DROP VIEW IF EXISTS `user_stats_all_vw`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `user_stats_all_vw` (
  `user_id` tinyint NOT NULL,
  `matches` tinyint NOT NULL,
  `wins` tinyint NOT NULL,
  `loses` tinyint NOT NULL,
  `racks_for` tinyint NOT NULL,
  `racks_against` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `user_stats_away_vw`
--

DROP TABLE IF EXISTS `user_stats_away_vw`;
/*!50001 DROP VIEW IF EXISTS `user_stats_away_vw`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `user_stats_away_vw` (
  `user_id` tinyint NOT NULL,
  `player_away_id` tinyint NOT NULL,
  `matches` tinyint NOT NULL,
  `wins` tinyint NOT NULL,
  `loses` tinyint NOT NULL,
  `racks_for` tinyint NOT NULL,
  `racks_against` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `user_stats_challenge_vw`
--

DROP TABLE IF EXISTS `user_stats_challenge_vw`;
/*!50001 DROP VIEW IF EXISTS `user_stats_challenge_vw`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `user_stats_challenge_vw` (
  `user_id` tinyint NOT NULL,
  `matches` tinyint NOT NULL,
  `wins` tinyint NOT NULL,
  `loses` tinyint NOT NULL,
  `racks_for` tinyint NOT NULL,
  `racks_against` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `user_stats_division_vw`
--

DROP TABLE IF EXISTS `user_stats_division_vw`;
/*!50001 DROP VIEW IF EXISTS `user_stats_division_vw`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `user_stats_division_vw` (
  `user_id` tinyint NOT NULL,
  `division_id` tinyint NOT NULL,
  `matches` tinyint NOT NULL,
  `wins` tinyint NOT NULL,
  `loses` tinyint NOT NULL,
  `racks_for` tinyint NOT NULL,
  `racks_against` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `user_stats_handicap_all_vw`
--

DROP TABLE IF EXISTS `user_stats_handicap_all_vw`;
/*!50001 DROP VIEW IF EXISTS `user_stats_handicap_all_vw`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `user_stats_handicap_all_vw` (
  `user_id` tinyint NOT NULL,
  `opponent_handicap` tinyint NOT NULL,
  `matches` tinyint NOT NULL,
  `wins` tinyint NOT NULL,
  `loses` tinyint NOT NULL,
  `racks_for` tinyint NOT NULL,
  `racks_against` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `user_stats_handicap_away_vw`
--

DROP TABLE IF EXISTS `user_stats_handicap_away_vw`;
/*!50001 DROP VIEW IF EXISTS `user_stats_handicap_away_vw`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `user_stats_handicap_away_vw` (
  `user_id` tinyint NOT NULL,
  `opponent_handicap` tinyint NOT NULL,
  `season_id` tinyint NOT NULL,
  `matches` tinyint NOT NULL,
  `wins` tinyint NOT NULL,
  `loses` tinyint NOT NULL,
  `racks_for` tinyint NOT NULL,
  `racks_against` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `user_stats_handicap_challenge_vw`
--

DROP TABLE IF EXISTS `user_stats_handicap_challenge_vw`;
/*!50001 DROP VIEW IF EXISTS `user_stats_handicap_challenge_vw`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `user_stats_handicap_challenge_vw` (
  `user_id` tinyint NOT NULL,
  `opponent_handicap` tinyint NOT NULL,
  `matches` tinyint NOT NULL,
  `wins` tinyint NOT NULL,
  `loses` tinyint NOT NULL,
  `racks_for` tinyint NOT NULL,
  `racks_against` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `user_stats_handicap_division_vw`
--

DROP TABLE IF EXISTS `user_stats_handicap_division_vw`;
/*!50001 DROP VIEW IF EXISTS `user_stats_handicap_division_vw`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `user_stats_handicap_division_vw` (
  `user_id` tinyint NOT NULL,
  `opponent_handicap` tinyint NOT NULL,
  `division_id` tinyint NOT NULL,
  `matches` tinyint NOT NULL,
  `wins` tinyint NOT NULL,
  `loses` tinyint NOT NULL,
  `racks_for` tinyint NOT NULL,
  `racks_against` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `user_stats_handicap_home_vw`
--

DROP TABLE IF EXISTS `user_stats_handicap_home_vw`;
/*!50001 DROP VIEW IF EXISTS `user_stats_handicap_home_vw`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `user_stats_handicap_home_vw` (
  `user_id` tinyint NOT NULL,
  `opponent_handicap` tinyint NOT NULL,
  `season_id` tinyint NOT NULL,
  `matches` tinyint NOT NULL,
  `wins` tinyint NOT NULL,
  `loses` tinyint NOT NULL,
  `racks_for` tinyint NOT NULL,
  `racks_against` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `user_stats_handicap_season_vw`
--

DROP TABLE IF EXISTS `user_stats_handicap_season_vw`;
/*!50001 DROP VIEW IF EXISTS `user_stats_handicap_season_vw`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `user_stats_handicap_season_vw` (
  `user_id` tinyint NOT NULL,
  `opponent_handicap` tinyint NOT NULL,
  `season_id` tinyint NOT NULL,
  `matches` tinyint NOT NULL,
  `wins` tinyint NOT NULL,
  `loses` tinyint NOT NULL,
  `racks_for` tinyint NOT NULL,
  `racks_against` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `user_stats_home_vw`
--

DROP TABLE IF EXISTS `user_stats_home_vw`;
/*!50001 DROP VIEW IF EXISTS `user_stats_home_vw`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `user_stats_home_vw` (
  `user_id` tinyint NOT NULL,
  `player_home_id` tinyint NOT NULL,
  `matches` tinyint NOT NULL,
  `wins` tinyint NOT NULL,
  `loses` tinyint NOT NULL,
  `racks_for` tinyint NOT NULL,
  `racks_against` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `user_stats_season_vw`
--

DROP TABLE IF EXISTS `user_stats_season_vw`;
/*!50001 DROP VIEW IF EXISTS `user_stats_season_vw`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `user_stats_season_vw` (
  `user_id` tinyint NOT NULL,
  `season_id` tinyint NOT NULL,
  `matches` tinyint NOT NULL,
  `wins` tinyint NOT NULL,
  `loses` tinyint NOT NULL,
  `racks_for` tinyint NOT NULL,
  `racks_against` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `user_stats_union_handicap_vw`
--

DROP TABLE IF EXISTS `user_stats_union_handicap_vw`;
/*!50001 DROP VIEW IF EXISTS `user_stats_union_handicap_vw`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `user_stats_union_handicap_vw` (
  `user_id` tinyint NOT NULL,
  `opponent_handicap` tinyint NOT NULL,
  `season_id` tinyint NOT NULL,
  `matches` tinyint NOT NULL,
  `wins` tinyint NOT NULL,
  `loses` tinyint NOT NULL,
  `racks_for` tinyint NOT NULL,
  `racks_against` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `user_stats_union_vw`
--

DROP TABLE IF EXISTS `user_stats_union_vw`;
/*!50001 DROP VIEW IF EXISTS `user_stats_union_vw`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `user_stats_union_vw` (
  `user_id` tinyint NOT NULL,
  `player_home_id` tinyint NOT NULL,
  `matches` tinyint NOT NULL,
  `wins` tinyint NOT NULL,
  `loses` tinyint NOT NULL,
  `racks_for` tinyint NOT NULL,
  `racks_against` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `user_stats_vw`
--

DROP TABLE IF EXISTS `user_stats_vw`;
/*!50001 DROP VIEW IF EXISTS `user_stats_vw`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `user_stats_vw` (
  `user_id` tinyint NOT NULL,
  `player_id` tinyint NOT NULL,
  `matches` tinyint NOT NULL,
  `wins` tinyint NOT NULL,
  `loses` tinyint NOT NULL,
  `racks_for` tinyint NOT NULL,
  `racks_against` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `login` varchar(256) NOT NULL,
  `role` varchar(32) NOT NULL DEFAULT 'PLAYER',
  `first_name` varchar(128) DEFAULT NULL,
  `last_name` varchar(128) DEFAULT NULL,
  `email` varchar(256) DEFAULT NULL,
  `passwd` varchar(256) DEFAULT NULL,
  `status` varchar(255) NOT NULL DEFAULT 'ACTIVE',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `login` (`login`)
) ENGINE=InnoDB AUTO_INCREMENT=9000 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Final view structure for view `player_away_result_vw`
--

/*!50001 DROP TABLE IF EXISTS `player_away_result_vw`*/;
/*!50001 DROP VIEW IF EXISTS `player_away_result_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`league`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `player_away_result_vw` AS select `r`.`player_away_id` AS `player_away_id`,`p`.`user_id` AS `user_id`,(case when (`r`.`away_racks` > `r`.`home_racks`) then 1 else 0 end) AS `win`,(case when (`r`.`away_racks` < `r`.`home_racks`) then 1 else 0 end) AS `lost`,`r`.`away_racks` AS `racks_for`,`r`.`home_racks` AS `racks_against`,`o`.`handicap` AS `opponent_handicap` from ((`player_result` `r` join `player` `p` on((`r`.`player_away_id` = `p`.`player_id`))) join `player` `o` on((`r`.`player_home_id` = `o`.`player_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `player_home_result_vw`
--

/*!50001 DROP TABLE IF EXISTS `player_home_result_vw`*/;
/*!50001 DROP VIEW IF EXISTS `player_home_result_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`league`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `player_home_result_vw` AS select `r`.`player_home_id` AS `player_home_id`,`p`.`user_id` AS `user_id`,(case when (`r`.`home_racks` > `r`.`away_racks`) then 1 else 0 end) AS `win`,(case when (`r`.`home_racks` < `r`.`away_racks`) then 1 else 0 end) AS `lost`,`r`.`home_racks` AS `racks_for`,`r`.`away_racks` AS `racks_against`,`o`.`handicap` AS `opponent_handicap` from ((`player_result` `r` join `player` `p` on((`r`.`player_home_id` = `p`.`player_id`))) join `player` `o` on((`r`.`player_away_id` = `o`.`player_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `team_away_result_vw`
--

/*!50001 DROP TABLE IF EXISTS `team_away_result_vw`*/;
/*!50001 DROP VIEW IF EXISTS `team_away_result_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`league`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `team_away_result_vw` AS select `m`.`away_team_id` AS `team_id`,`m`.`season_id` AS `season_id`,(case when (`r`.`home_racks` > `r`.`away_racks`) then 0 else 1 end) AS `win`,(case when (`r`.`home_racks` < `r`.`away_racks`) then 0 else 1 end) AS `lost`,`r`.`home_racks` AS `racks_for`,`r`.`away_racks` AS `racks_against` from ((`team_result` `r` join `team_match` `m` on((`r`.`team_match_id` = `m`.`team_match_id`)) )) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `team_away_stats_vw`
--

/*!50001 DROP TABLE IF EXISTS `team_away_stats_vw`*/;
/*!50001 DROP VIEW IF EXISTS `team_away_stats_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`league`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `team_away_stats_vw` AS select `team_away_result_vw`.`team_id` AS `team_id`,`team_away_result_vw`.`season_id` AS `season_id`,sum(`team_away_result_vw`.`win`) AS `wins`,sum(`team_away_result_vw`.`lost`) AS `loses`,sum(`team_away_result_vw`.`racks_for`) AS `racks_for`,sum(`team_away_result_vw`.`racks_against`) AS `racks_against` from `team_away_result_vw` group by `team_away_result_vw`.`team_id`,`team_away_result_vw`.`season_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `team_home_result_vw`
--

/*!50001 DROP TABLE IF EXISTS `team_home_result_vw`*/;
/*!50001 DROP VIEW IF EXISTS `team_home_result_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`league`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `team_home_result_vw` AS select `m`.`home_team_id` AS `team_id`,`m`.`season_id` AS `season_id`,(case when (`r`.`home_racks` > `r`.`away_racks`) then 1 else 0 end) AS `win`,(case when (`r`.`home_racks` < `r`.`away_racks`) then 1 else 0 end) AS `lost`,`r`.`home_racks` AS `racks_for`,`r`.`away_racks` AS `racks_against` from ((`team_result` `r` join `team_match` `m` on((`r`.`team_match_id` = `m`.`team_match_id`)) )) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `team_home_stats_vw`
--

/*!50001 DROP TABLE IF EXISTS `team_home_stats_vw`*/;
/*!50001 DROP VIEW IF EXISTS `team_home_stats_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`league`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `team_home_stats_vw` AS select `team_home_result_vw`.`team_id` AS `team_id`,`team_home_result_vw`.`season_id` AS `season_id`,sum(`team_home_result_vw`.`win`) AS `wins`,sum(`team_home_result_vw`.`lost`) AS `loses`,sum(`team_home_result_vw`.`racks_for`) AS `racks_for`,sum(`team_home_result_vw`.`racks_against`) AS `racks_against` from `team_home_result_vw` group by `team_home_result_vw`.`team_id`,`team_home_result_vw`.`season_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `team_set_away_vw`
--

/*!50001 DROP TABLE IF EXISTS `team_set_away_vw`*/;
/*!50001 DROP VIEW IF EXISTS `team_set_away_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`league`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `team_set_away_vw` AS select `p`.`team_id` AS `team_id`,`p`.`season_id` AS `season_id`,sum(`a`.`win`) AS `setWins`,sum(`a`.`lost`) AS `setLoses` from (`player_away_result_vw` `a` join `player` `p` on((`a`.`player_away_id` = `p`.`player_id`))) group by `p`.`team_id`,`p`.`season_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `team_set_home_vw`
--

/*!50001 DROP TABLE IF EXISTS `team_set_home_vw`*/;
/*!50001 DROP VIEW IF EXISTS `team_set_home_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`league`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `team_set_home_vw` AS select `p`.`team_id` AS `team_id`,`p`.`season_id` AS `season_id`,sum(`a`.`win`) AS `setWins`,sum(`a`.`lost`) AS `setLoses` from (`player_home_result_vw` `a` join `player` `p` on((`a`.`player_home_id` = `p`.`player_id`))) group by `p`.`team_id`,`p`.`season_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `team_set_vw`
--

/*!50001 DROP TABLE IF EXISTS `team_set_vw`*/;
/*!50001 DROP VIEW IF EXISTS `team_set_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`league`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `team_set_vw` AS select `home`.`team_id` AS `team_id`,`home`.`season_id` AS `season_id`,cast((`home`.`setWins` + `away`.`setWins`) as unsigned) AS `setWins`,cast((`home`.`setLoses` + `away`.`setLoses`) as unsigned) AS `setLoses` from (`team_set_home_vw` `home` join `team_set_away_vw` `away` on(((`home`.`team_id` = `away`.`team_id`) and (`home`.`season_id` = `away`.`season_id`)))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `team_stats_vw`
--

/*!50001 DROP TABLE IF EXISTS `team_stats_vw`*/;
/*!50001 DROP VIEW IF EXISTS `team_stats_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`league`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `team_stats_vw` AS select `home`.`team_id` AS `team_id`,`home`.`season_id` AS `season_id`,cast((sum(`home`.`wins`) + sum(`away`.`wins`)) as unsigned) AS `wins`,cast((sum(`home`.`loses`) + sum(`away`.`loses`)) as unsigned) AS `loses`,cast((sum(`home`.`racks_for`) + sum(`away`.`racks_for`)) as unsigned) AS `racks_for`,cast((sum(`home`.`racks_against`) + sum(`away`.`racks_against`)) as unsigned) AS `racks_against` from (`team_home_stats_vw` `home` join `team_away_stats_vw` `away` on(((`home`.`team_id` = `away`.`team_id`) and (`home`.`season_id` = `away`.`season_id`)))) group by `home`.`team_id`,`home`.`season_id`,`away`.`team_id`,`away`.`season_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `user_stats_all_vw`
--

/*!50001 DROP TABLE IF EXISTS `user_stats_all_vw`*/;
/*!50001 DROP VIEW IF EXISTS `user_stats_all_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`league`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `user_stats_all_vw` AS select `user_stats_vw`.`user_id` AS `user_id`,sum(`user_stats_vw`.`matches`) AS `matches`,sum(`user_stats_vw`.`wins`) AS `wins`,sum(`user_stats_vw`.`loses`) AS `loses`,sum(`user_stats_vw`.`racks_for`) AS `racks_for`,sum(`user_stats_vw`.`racks_against`) AS `racks_against` from `user_stats_vw` group by `user_stats_vw`.`user_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `user_stats_away_vw`
--

/*!50001 DROP TABLE IF EXISTS `user_stats_away_vw`*/;
/*!50001 DROP VIEW IF EXISTS `user_stats_away_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`league`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `user_stats_away_vw` AS select `player_away_result_vw`.`user_id` AS `user_id`,`player_away_result_vw`.`player_away_id` AS `player_away_id`,count(0) AS `matches`,sum(`player_away_result_vw`.`win`) AS `wins`,sum(`player_away_result_vw`.`lost`) AS `loses`,sum(`player_away_result_vw`.`racks_for`) AS `racks_for`,sum(`player_away_result_vw`.`racks_against`) AS `racks_against` from `player_away_result_vw` group by `player_away_result_vw`.`player_away_id`,`player_away_result_vw`.`user_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `user_stats_challenge_vw`
--

/*!50001 DROP TABLE IF EXISTS `user_stats_challenge_vw`*/;
/*!50001 DROP VIEW IF EXISTS `user_stats_challenge_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`league`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `user_stats_challenge_vw` AS select `s`.`user_id` AS `user_id`,sum(`s`.`matches`) AS `matches`,sum(`s`.`wins`) AS `wins`,sum(`s`.`loses`) AS `loses`,sum(`s`.`racks_for`) AS `racks_for`,sum(`s`.`racks_against`) AS `racks_against` from ((`user_stats_vw` `s` join `player` `p` on((`p`.`player_id` = `s`.`player_id`))) join `division` `d` on((`p`.`division_id` = `d`.`division_id`))) where (`d`.`division_type` like '%CHALLENGE%') group by `s`.`user_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `user_stats_division_vw`
--

/*!50001 DROP TABLE IF EXISTS `user_stats_division_vw`*/;
/*!50001 DROP VIEW IF EXISTS `user_stats_division_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`league`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `user_stats_division_vw` AS select `s`.`user_id` AS `user_id`,`p`.`division_id` AS `division_id`,sum(`s`.`matches`) AS `matches`,sum(`s`.`wins`) AS `wins`,sum(`s`.`loses`) AS `loses`,sum(`s`.`racks_for`) AS `racks_for`,sum(`s`.`racks_against`) AS `racks_against` from (`user_stats_vw` `s` join `player` `p` on((`p`.`player_id` = `s`.`player_id`))) group by `s`.`user_id`,`p`.`division_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `user_stats_handicap_all_vw`
--

/*!50001 DROP TABLE IF EXISTS `user_stats_handicap_all_vw`*/;
/*!50001 DROP VIEW IF EXISTS `user_stats_handicap_all_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`league`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `user_stats_handicap_all_vw` AS select `user_stats_union_handicap_vw`.`user_id` AS `user_id`,`user_stats_union_handicap_vw`.`opponent_handicap` AS `opponent_handicap`,sum(`user_stats_union_handicap_vw`.`matches`) AS `matches`,sum(`user_stats_union_handicap_vw`.`wins`) AS `wins`,sum(`user_stats_union_handicap_vw`.`loses`) AS `loses`,sum(`user_stats_union_handicap_vw`.`racks_for`) AS `racks_for`,sum(`user_stats_union_handicap_vw`.`racks_against`) AS `racks_against` from `user_stats_union_handicap_vw` group by `user_stats_union_handicap_vw`.`user_id`,`user_stats_union_handicap_vw`.`opponent_handicap` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `user_stats_handicap_away_vw`
--

/*!50001 DROP TABLE IF EXISTS `user_stats_handicap_away_vw`*/;
/*!50001 DROP VIEW IF EXISTS `user_stats_handicap_away_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`league`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `user_stats_handicap_away_vw` AS select `p`.`user_id` AS `user_id`,`away`.`opponent_handicap` AS `opponent_handicap`,`p`.`season_id` AS `season_id`,count(0) AS `matches`,sum(`away`.`win`) AS `wins`,sum(`away`.`lost`) AS `loses`,sum(`away`.`racks_for`) AS `racks_for`,sum(`away`.`racks_against`) AS `racks_against` from (`player_away_result_vw` `away` join `player` `p` on((`away`.`player_away_id` = `p`.`player_id`))) group by `away`.`player_away_id`,`p`.`user_id`,`away`.`opponent_handicap`,`p`.`season_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `user_stats_handicap_challenge_vw`
--

/*!50001 DROP TABLE IF EXISTS `user_stats_handicap_challenge_vw`*/;
/*!50001 DROP VIEW IF EXISTS `user_stats_handicap_challenge_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`league`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `user_stats_handicap_challenge_vw` AS select `u`.`user_id` AS `user_id`,`u`.`opponent_handicap` AS `opponent_handicap`,sum(`u`.`matches`) AS `matches`,sum(`u`.`wins`) AS `wins`,sum(`u`.`loses`) AS `loses`,sum(`u`.`racks_for`) AS `racks_for`,sum(`u`.`racks_against`) AS `racks_against` from (`user_stats_union_handicap_vw` `u` join `season` `s` on((`s`.`season_id` = `u`.`season_id`))) where (`s`.`name` like '%CHALLENGE%') group by `u`.`user_id`,`u`.`opponent_handicap` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `user_stats_handicap_division_vw`
--

/*!50001 DROP TABLE IF EXISTS `user_stats_handicap_division_vw`*/;
/*!50001 DROP VIEW IF EXISTS `user_stats_handicap_division_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`league`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `user_stats_handicap_division_vw` AS select `u`.`user_id` AS `user_id`,`u`.`opponent_handicap` AS `opponent_handicap`,`s`.`division_id` AS `division_id`,sum(`u`.`matches`) AS `matches`,sum(`u`.`wins`) AS `wins`,sum(`u`.`loses`) AS `loses`,sum(`u`.`racks_for`) AS `racks_for`,sum(`u`.`racks_against`) AS `racks_against` from (`user_stats_union_handicap_vw` `u` join `season` `s` on((`s`.`season_id` = `u`.`season_id`))) group by `u`.`user_id`,`u`.`opponent_handicap`,`s`.`division_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `user_stats_handicap_home_vw`
--

/*!50001 DROP TABLE IF EXISTS `user_stats_handicap_home_vw`*/;
/*!50001 DROP VIEW IF EXISTS `user_stats_handicap_home_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`league`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `user_stats_handicap_home_vw` AS select `p`.`user_id` AS `user_id`,`home`.`opponent_handicap` AS `opponent_handicap`,`p`.`season_id` AS `season_id`,count(0) AS `matches`,sum(`home`.`win`) AS `wins`,sum(`home`.`lost`) AS `loses`,sum(`home`.`racks_for`) AS `racks_for`,sum(`home`.`racks_against`) AS `racks_against` from (`player_home_result_vw` `home` join `player` `p` on((`home`.`player_home_id` = `p`.`player_id`))) group by `home`.`player_home_id`,`p`.`user_id`,`home`.`opponent_handicap`,`p`.`season_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `user_stats_handicap_season_vw`
--

/*!50001 DROP TABLE IF EXISTS `user_stats_handicap_season_vw`*/;
/*!50001 DROP VIEW IF EXISTS `user_stats_handicap_season_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`league`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `user_stats_handicap_season_vw` AS select `user_stats_union_handicap_vw`.`user_id` AS `user_id`,`user_stats_union_handicap_vw`.`opponent_handicap` AS `opponent_handicap`,`user_stats_union_handicap_vw`.`season_id` AS `season_id`,sum(`user_stats_union_handicap_vw`.`matches`) AS `matches`,sum(`user_stats_union_handicap_vw`.`wins`) AS `wins`,sum(`user_stats_union_handicap_vw`.`loses`) AS `loses`,sum(`user_stats_union_handicap_vw`.`racks_for`) AS `racks_for`,sum(`user_stats_union_handicap_vw`.`racks_against`) AS `racks_against` from `user_stats_union_handicap_vw` group by `user_stats_union_handicap_vw`.`user_id`,`user_stats_union_handicap_vw`.`opponent_handicap`,`user_stats_union_handicap_vw`.`season_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `user_stats_home_vw`
--

/*!50001 DROP TABLE IF EXISTS `user_stats_home_vw`*/;
/*!50001 DROP VIEW IF EXISTS `user_stats_home_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`league`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `user_stats_home_vw` AS select `player_home_result_vw`.`user_id` AS `user_id`,`player_home_result_vw`.`player_home_id` AS `player_home_id`,count(0) AS `matches`,sum(`player_home_result_vw`.`win`) AS `wins`,sum(`player_home_result_vw`.`lost`) AS `loses`,sum(`player_home_result_vw`.`racks_for`) AS `racks_for`,sum(`player_home_result_vw`.`racks_against`) AS `racks_against` from `player_home_result_vw` group by `player_home_result_vw`.`player_home_id`,`player_home_result_vw`.`user_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `user_stats_season_vw`
--

/*!50001 DROP TABLE IF EXISTS `user_stats_season_vw`*/;
/*!50001 DROP VIEW IF EXISTS `user_stats_season_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`league`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `user_stats_season_vw` AS select `s`.`user_id` AS `user_id`,`p`.`season_id` AS `season_id`,sum(`s`.`matches`) AS `matches`,sum(`s`.`wins`) AS `wins`,sum(`s`.`loses`) AS `loses`,sum(`s`.`racks_for`) AS `racks_for`,sum(`s`.`racks_against`) AS `racks_against` from (`user_stats_vw` `s` join `player` `p` on((`p`.`player_id` = `s`.`player_id`))) group by `s`.`user_id`,`p`.`season_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `user_stats_union_handicap_vw`
--

/*!50001 DROP TABLE IF EXISTS `user_stats_union_handicap_vw`*/;
/*!50001 DROP VIEW IF EXISTS `user_stats_union_handicap_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`league`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `user_stats_union_handicap_vw` AS select `user_stats_handicap_home_vw`.`user_id` AS `user_id`,`user_stats_handicap_home_vw`.`opponent_handicap` AS `opponent_handicap`,`user_stats_handicap_home_vw`.`season_id` AS `season_id`,`user_stats_handicap_home_vw`.`matches` AS `matches`,`user_stats_handicap_home_vw`.`wins` AS `wins`,`user_stats_handicap_home_vw`.`loses` AS `loses`,`user_stats_handicap_home_vw`.`racks_for` AS `racks_for`,`user_stats_handicap_home_vw`.`racks_against` AS `racks_against` from `user_stats_handicap_home_vw` union all select `user_stats_handicap_away_vw`.`user_id` AS `user_id`,`user_stats_handicap_away_vw`.`opponent_handicap` AS `opponent_handicap`,`user_stats_handicap_away_vw`.`season_id` AS `season_id`,`user_stats_handicap_away_vw`.`matches` AS `matches`,`user_stats_handicap_away_vw`.`wins` AS `wins`,`user_stats_handicap_away_vw`.`loses` AS `loses`,`user_stats_handicap_away_vw`.`racks_for` AS `racks_for`,`user_stats_handicap_away_vw`.`racks_against` AS `racks_against` from `user_stats_handicap_away_vw` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `user_stats_union_vw`
--

/*!50001 DROP TABLE IF EXISTS `user_stats_union_vw`*/;
/*!50001 DROP VIEW IF EXISTS `user_stats_union_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`league`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `user_stats_union_vw` AS select `user_stats_home_vw`.`user_id` AS `user_id`,`user_stats_home_vw`.`player_home_id` AS `player_home_id`,`user_stats_home_vw`.`matches` AS `matches`,`user_stats_home_vw`.`wins` AS `wins`,`user_stats_home_vw`.`loses` AS `loses`,`user_stats_home_vw`.`racks_for` AS `racks_for`,`user_stats_home_vw`.`racks_against` AS `racks_against` from `user_stats_home_vw` union all select `user_stats_away_vw`.`user_id` AS `user_id`,`user_stats_away_vw`.`player_away_id` AS `player_away_id`,`user_stats_away_vw`.`matches` AS `matches`,`user_stats_away_vw`.`wins` AS `wins`,`user_stats_away_vw`.`loses` AS `loses`,`user_stats_away_vw`.`racks_for` AS `racks_for`,`user_stats_away_vw`.`racks_against` AS `racks_against` from `user_stats_away_vw` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `user_stats_vw`
--

/*!50001 DROP TABLE IF EXISTS `user_stats_vw`*/;
/*!50001 DROP VIEW IF EXISTS `user_stats_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`league`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `user_stats_vw` AS select `user_stats_union_vw`.`user_id` AS `user_id`,`user_stats_union_vw`.`player_home_id` AS `player_id`,sum(`user_stats_union_vw`.`matches`) AS `matches`,sum(`user_stats_union_vw`.`wins`) AS `wins`,sum(`user_stats_union_vw`.`loses`) AS `loses`,sum(`user_stats_union_vw`.`racks_for`) AS `racks_for`,sum(`user_stats_union_vw`.`racks_against`) AS `racks_against` from `user_stats_union_vw` group by `user_stats_union_vw`.`user_id`,`user_stats_union_vw`.`player_home_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-06-13 11:19:15
