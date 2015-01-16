 CREATE TABLE `season` (
  `season_id` int(11) NOT NULL AUTO_INCREMENT,
  `division_id` int(11) NOT NULL,
   name varchar(128) NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NULL,
  `rounds` int(11) NOT NULL,
  `updated_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` int(11) DEFAULT '1',
  PRIMARY KEY (`season_id`),
CONSTRAINT FOREIGN KEY (`division_id`) REFERENCES division(division_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=MyISAM AUTO_INCREMENT=4000 DEFAULT CHARSET=latin1;
