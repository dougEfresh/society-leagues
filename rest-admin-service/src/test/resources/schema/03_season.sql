 CREATE TABLE season (
  season_id    INT          NOT NULL GENERATED ALWAYS AS IDENTITY,
  division_id  INT          NOT NULL CONSTRAINT S_DIV_FK  REFERENCES division ON DELETE CASCADE ON UPDATE RESTRICT,
  name         VARCHAR(128) NOT NULL,
  start_date   DATE         NOT NULL,
  end_date     DATE,
  updated_date TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  status       INT                   DEFAULT 1,
  PRIMARY KEY (season_id)
 )
