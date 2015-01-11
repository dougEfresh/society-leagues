CREATE TABLE `division` (
  `division_id` int(11) NOT NULL AUTO_INCREMENT,
  `league_id` int(11) NOT NULL,
  `type` varchar(64) NOT NULL,
  PRIMARY KEY (`division_id`),
  CONSTRAINT FOREIGN KEY (`league_id`) REFERENCES league(league_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=MyISAM AUTO_INCREMENT=3000 DEFAULT CHARSET=latin1 ;
