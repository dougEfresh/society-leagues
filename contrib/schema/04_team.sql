CREATE TABLE `team` (
  `team_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `active` boolean DEFAULT true,
  `default_division_id` varchar(34) NOT NULL,
  `updated_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`team_id`),
  CONSTRAINT FOREIGN KEY (`default_division_id`) REFERENCES division(division_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=MyISAM AUTO_INCREMENT=1000 DEFAULT CHARSET=latin1 ;

