CREATE TABLE league (
  league_id int NOT NULL GENERATED ALWAYS AS IDENTITY,
  league_type varchar(32) NOT NULL,
  PRIMARY KEY (league_id)
)
