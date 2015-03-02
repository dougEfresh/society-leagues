CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `login` varchar(256) NOT NULL,
  `role` varchar(32) NOT NULL DEFAULT 'PLAYER',
  `first_name` varchar(128) DEFAULT NULL,
  `last_name` varchar(128) DEFAULT NULL,
  `email` varchar(256) DEFAULT NULL,
  `password` varchar(256) DEFAULT NULL,
   `status` int not null default '1',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `login_unique` (`login`)
) ENGINE=MyISAM AUTO_INCREMENT=90000 DEFAULT CHARSET=latin1 