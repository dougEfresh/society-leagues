package com.society.leagues;

import com.society.leagues.client.api.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Schema {

    @Autowired JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        createDb(jdbcTemplate);
        createAccounts(jdbcTemplate);
    }

    static boolean createdSchema;
    static boolean createdAccounts;
    static final String token = "create table token_cache(token varchar(64),\n" +
            "  player varchar(4096) NOT NULL ,\n" +
            "  created_date timestamp,\n" +
            "  PRIMARY KEY (token)\n" +
            ")\n";

    static final String user = "CREATE TABLE users (\n" +
            "  user_id int NOT NULL AUTO_INCREMENT(1000) PRIMARY KEY,\n" +
            "  login varchar(256) NOT NULL,\n" +
            "  role varchar(32) NOT NULL DEFAULT 'PLAYER',\n" +
            "  first_name varchar(128) DEFAULT NULL,\n" +
            "  last_name varchar(128) DEFAULT NULL,\n" +
            "  email varchar(256) DEFAULT NULL,\n" +
            "  passwd varchar(256) DEFAULT NULL,\n" +
            "  status int not null default 1,\n" +
            "  PRIMARY KEY (user_id),\n" +
            "  UNIQUE  (login)\n" +
            ")";
    
    static final String division = "CREATE TABLE division (\n" +
            "  division_id int NOT NULL AUTO_INCREMENT(5000) PRIMARY KEY,\n" +
            "  league_type varchar(255) NOT NULL,\n" +
            "  division_type  varchar(64) NOT NULL,\n" +
            "  PRIMARY KEY (division_id)\n" +
            ")\n";
    
    static final String season = " CREATE TABLE season (\n" +
            "  season_id    INT          NOT NULL AUTO_INCREMENT(10000) PRIMARY KEY,\n" +
            "  name         VARCHAR(128) NOT NULL,\n" +
            "  start_date   TIMESTAMP         NOT NULL,\n" +
            "  end_date     DATE,\n" +
            "  updated TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
            "  rounds       INT          NOT NULL,\n" +
            "  season_status       varchar(255) NOT NULL,\n" +
            "  PRIMARY KEY (season_id)\n" +
            " )\n";
    
    static final String team = "CREATE TABLE team (\n" +
            "  team_id int NOT NULL AUTO_INCREMENT(15000) PRIMARY KEY,\n" +
            "  name varchar(128) NOT NULL,\n" +
            "  default_division_id int  NOT NULL CONSTRAINT TEAM_DIV_FK REFERENCES division ON DELETE CASCADE ON UPDATE RESTRICT,\n" +
            "  created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
            "  PRIMARY KEY (team_id)\n" +
            ")\n" +
            "\n";
    
    static final String player = "create table player  (\n" +
            " player_id int NOT NULL AUTO_INCREMENT(20000) PRIMARY KEY,\n" +
            " season_id int NOT NULL,\n" +
            " division_id INT  NOT NULL CONSTRAINT S_DIV_FK  REFERENCES division ON DELETE CASCADE ON UPDATE RESTRICT,\n" +
            " user_id int NOT NULL,\n" +
            " team_id int NOT NULL,\n" +
            " handicap int NOT NULL,\n" +
            " player_status varchar(255) NOT NULL,\n" +
            " PRIMARY KEY (player_id),\n" +
            "CONSTRAINT P_S_FK FOREIGN KEY (season_id) REFERENCES season(season_id) ON DELETE CASCADE ON UPDATE RESTRICT ,\n" +
            "CONSTRAINT P_U_FK FOREIGN KEY (user_id)   REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE RESTRICT ,\n" +
            "CONSTRAINT P_T_FK FOREIGN KEY (team_id)   REFERENCES team(team_id) ON DELETE CASCADE ON UPDATE RESTRICT\n" +
            ")\n";
    
      static final String team_match = "create table team_match  (\n" +
              " team_match_id int NOT NULL AUTO_INCREMENT(30000) PRIMARY KEY,\n" +
              " season_id int NOT NULL CONSTRAINT T_M_S_K REFERENCES season ON DELETE CASCADE,\n" +
              " home_team_id int NOT NULL CONSTRAINT HOME_K REFERENCES team ON DELETE CASCADE  ,\n" +
              " away_team_id int NOT NULL CONSTRAINT AWAY_K REFERENCES team ON DELETE CASCADE ,\n" +
              " match_date date not null, " +
              " racks int not null DEFAULT 0," +
              " win int," +
              " PRIMARY KEY (team_match_id)" +
            ")\n";

    static final String challenge = "create table challenge  (\n" +
              " challenge_id int NOT NULL AUTO_INCREMENT(40000) PRIMARY KEY,\n" +
              " challenger_player_id int NOT NULL CONSTRAINT PM_S_K REFERENCES player ON DELETE CASCADE,\n" +
              " opponent_player_id int NOT NULL CONSTRAINT PM_K REFERENCES player ON DELETE CASCADE,\n" +
              " slot int not null,\n" +
              " challenge_date timestamp not null,\n" +
              " status varchar(255) not null,\n" +
              " team_match_id INT CONSTRAINT TM_C REFERENCES team_match ON DELETE CASCADE,\n" +
              " PRIMARY KEY (challenge_id)" +
            ")\n";
    
    static final String slot = "create table slot  (\n" +
              " slot_id int NOT NULL\n" +
            " challenge_date timestamp not null,\n" +
              " PRIMARY KEY (slot_id,challenge_date)" +
            ")\n";

    
    public static void createDb(JdbcTemplate jdbcTemplate) {
        if (createdSchema)
            return;

        jdbcTemplate.update(token);
        jdbcTemplate.update(user);
        jdbcTemplate.update(division);
        jdbcTemplate.update(season);
        jdbcTemplate.update(team);
        jdbcTemplate.update(player);
        jdbcTemplate.update(team_match);
        jdbcTemplate.update(challenge);
        createdSchema = true;
    }

    public static final String NORMAL_USER = "email_608@domain.com";
    public static final String NORMAL_PASS = "password_608";
    public static final String ADMIN_USER =  "email_528@domain.com";
    public static final String ADMIN_PASS =  "password_528";
    
    public static void createAccounts(JdbcTemplate jdbcTemplate) {
        if (createdAccounts)
            return;

        jdbcTemplate.update("INSERT INTO users (login,role,passwd)" +
                        " VALUES (?,?,?)",
                ADMIN_USER, Role.ADMIN.name(),ADMIN_PASS);

        jdbcTemplate.update("INSERT INTO users (login,role,passwd) " +
                        "VALUES (?,?,?)",
                NORMAL_USER, Role.PLAYER.name(), NORMAL_PASS);

        createdAccounts = true;
    }
}
