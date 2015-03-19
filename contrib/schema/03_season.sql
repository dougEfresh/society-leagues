 CREATE TABLE `season` (
  `season_id` int(11) NOT NULL AUTO_INCREMENT,
   name varchar(128) NOT NULL,
  `start_date` challengeTimes NOT NULL,
  `end_date` challengeTimes NULL,
  `rounds` int(11) NOT NULL,
  `updated_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` int(11) DEFAULT '1',
  PRIMARY KEY (`season_id`)
) ENGINE=MyISAM AUTO_INCREMENT=4000 DEFAULT CHARSET=latin1;
