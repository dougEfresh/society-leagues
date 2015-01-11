create table token_cache(token varchar(64),
  player varchar(4096) NOT NULL ,
  created_date timestamp,
  PRIMARY KEY (token)
)

