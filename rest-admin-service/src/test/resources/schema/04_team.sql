CREATE TABLE team (
  team_id int NOT NULL GENERATED ALWAYS AS IDENTITY,
  name varchar(128) NOT NULL,
  active boolean DEFAULT true,
  default_division_id int  NOT NULL CONSTRAINT TEAM_DIV_FK REFERENCES division ON DELETE CASCADE ON UPDATE RESTRICT,
  updated_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (team_id)
)

