CREATE TABLE users (
user_id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
login varchar(256) NOT NULL,
role varchar(32) NOT NULL DEFAULT 'PLAYER',
first_name varchar(128) DEFAULT NULL,
last_name varchar(128) DEFAULT NULL,
email varchar(256) DEFAULT NULL,
passwd varchar(256) DEFAULT NULL,
status int not null default 1,
UNIQUE  (login)
)ENGINE=InnoDB AUTO_INCREMENT 2000;

CREATE TABLE division (
  division_id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
  league_type varchar(255) NOT NULL,
  division_type  varchar(64) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT 5000;


CREATE TABLE season (
  season_id    INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name         VARCHAR(128) NOT NULL,
  start_date   TIMESTAMP         NOT NULL,
  end_date     DATE,
  updated TIMESTAMP    NULL,
  rounds       INT          NOT NULL,
  season_status       varchar(255) NOT NULL
 ) ENGINE=InnoDB AUTO_INCREMENT 10000;

CREATE TABLE team (
  team_id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name varchar(128) NOT NULL,
  created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB AUTO_INCREMENT 15000;


create table player  (
 player_id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
 season_id int NOT NULL,
 division_id INT ,
 user_id int NOT NULL,
 team_id int NOT NULL,
 handicap int NOT NULL,
 start_date DATE null,
 end_date DATE null,
FOREIGN KEY (division_id) REFERENCES division(division_id) ON DELETE CASCADE ON UPDATE RESTRICT ,
FOREIGN KEY (season_id) REFERENCES season(season_id) ON DELETE CASCADE ON UPDATE RESTRICT ,
FOREIGN KEY (user_id)   REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE RESTRICT ,
FOREIGN KEY (team_id)   REFERENCES team(team_id) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT 20000;


create table team_match  (
 team_match_id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
 season_id int NOT NULL ,
 home_team_id int NOT NULL ,
 away_team_id int NOT NULL ,
 division_id int NOT NULL  ,
 match_date timestamp ,
FOREIGN KEY (season_id) REFERENCES season(season_id) ON DELETE CASCADE ON UPDATE RESTRICT ,
FOREIGN KEY (home_team_id)   REFERENCES team(team_id) ON DELETE CASCADE ON UPDATE RESTRICT ,
FOREIGN KEY (away_team_id)   REFERENCES team(team_id) ON DELETE CASCADE ON UPDATE RESTRICT ,
FOREIGN KEY (division_id)   REFERENCES division(division_id) ON DELETE CASCADE ON UPDATE RESTRICT

) ENGINE=InnoDB AUTO_INCREMENT 30000;

create table team_result (
 team_result_id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
 team_match_id int NOT NULL,
 home_racks int NOT NULL, away_racks int NOT NULL,
 FOREIGN KEY (team_match_id)    REFERENCES team_match(team_match_id) ON DELETE CASCADE ON UPDATE RESTRICT 
) ENGINE=InnoDB AUTO_INCREMENT 50000;

create table player_result (
 player_result_id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
 team_match_id int NOT NULL,
 player_home_id int NOT NULL,
 player_away_id int NOT NULL,
 home_racks int NOT NULL, away_racks int NOT NULL,
FOREIGN KEY (team_match_id)    REFERENCES team_match(team_match_id) ON DELETE CASCADE ON UPDATE RESTRICT ,
FOREIGN KEY (player_home_id)   REFERENCES player(player_id) ON DELETE CASCADE ON UPDATE RESTRICT ,
FOREIGN KEY (player_away_id)   REFERENCES player(player_id) ON DELETE CASCADE ON UPDATE RESTRICT

) ENGINE=InnoDB AUTO_INCREMENT 60000;

create table slot  (
 slot_id int NOT NULL  AUTO_INCREMENT PRIMARY KEY,
 slot_time timestamp not null,
 allocated int not null DEFAULT 0 
)ENGINE=InnoDB AUTO_INCREMENT 80000;


create table challenge  (
 challenge_id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
 player_challenger_id int not null,
 player_opponent_id int not null,
 slot_id int not null ,
 status varchar(255) not null,
FOREIGN KEY (player_opponent_id)   REFERENCES player(player_id) ON DELETE CASCADE ON UPDATE RESTRICT ,
FOREIGN KEY (player_challenger_id)   REFERENCES player(player_id) ON DELETE CASCADE ON UPDATE RESTRICT,
FOREIGN KEY (slot_id)   REFERENCES slot(slot_id) ON DELETE CASCADE ON UPDATE RESTRICT

) ENGINE=InnoDB AUTO_INCREMENT 40000;

